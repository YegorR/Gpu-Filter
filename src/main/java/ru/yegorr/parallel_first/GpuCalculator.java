package ru.yegorr.parallel_first;

import org.jocl.*;

import java.io.*;
import java.net.*;
import java.nio.file.*;

import static org.jocl.CL.*;

/**
 * User: RyazantsevEV<br>
 * Date: 10.10.2021<br>
 * Time: 16:24<br>
 * Делает фильтрацию через GPU
 */
public class GpuCalculator implements Calculator {

    private final static String KERNEL_SOURCE = "kernel.cl";

    private static boolean isInitialized = false;

    private cl_program program;

    private cl_command_queue commandQueue;

    private cl_context context;

    private float[] doFlatArray(float[][] array) {
        float[] result = new float[array.length * array[0].length];
        int i = 0;
        for (float[] row : array) {
            for (float elem : row) {
                result[i++] = elem;
            }
        }
        return result;
    }

    private float[][] transpose(float[][] array) {
        float[][] result = new float[array[0].length][array.length];
        for (int i = 0; i < result.length; ++i) {
            for (int j = 0; j < result[i].length; ++j) {
                result[i][j] = array[j][i];
            }
        }
        return result;
    }

    @Override
    public ResultDto calculate(float[][] intensity) {

        float[] flatIntensity = doFlatArray(transpose(intensity));
        float[] flatFilter = doFlatArray(GAUSS_FILTER);
        final int resultWidth = intensity.length - FILTER_WIDTH + 1;
        final int resultHeight = intensity[0].length - FILTER_HEIGHT + 1;
        float[] flatResult = new float[resultWidth * resultHeight];

        initializeOpenCl();

        long startTime = System.nanoTime();

        Pointer intensityPointer = Pointer.to(flatIntensity);
        Pointer gaussPointer = Pointer.to(flatFilter);
        Pointer resultPointer = Pointer.to(flatResult);

        cl_mem intensityMem =
                clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, (long)Sizeof.cl_float * flatIntensity.length, intensityPointer, null);

        cl_mem gaussMem =
                clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, (long)Sizeof.cl_float * flatFilter.length, gaussPointer, null);

        cl_mem resultMem =
                clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, (long)Sizeof.cl_float * flatResult.length, resultPointer, null);

        cl_kernel kernel = clCreateKernel(program, "mykernel", null);

        clSetKernelArg(kernel, 0, Sizeof.cl_mem, Pointer.to(intensityMem));
        clSetKernelArg(kernel, 1, Sizeof.cl_uint, Pointer.to(new int[]{intensity.length}));
        clSetKernelArg(kernel, 2, Sizeof.cl_mem, Pointer.to(gaussMem));
        clSetKernelArg(kernel, 3, Sizeof.cl_uint, Pointer.to(new int[]{FILTER_WIDTH}));
        clSetKernelArg(kernel, 4, Sizeof.cl_mem, Pointer.to(resultMem));

        clEnqueueNDRangeKernel(commandQueue, kernel, 2, null, new long[]{resultHeight, resultWidth}, null, 0, null, null);

        clEnqueueReadBuffer(commandQueue, resultMem, CL_TRUE, 0, (long)Sizeof.cl_float * flatResult.length, resultPointer, 0, null, null);

        clReleaseMemObject(intensityMem);
        clReleaseMemObject(gaussMem);
        clReleaseMemObject(resultMem);
        clReleaseKernel(kernel);

        long finishTime = System.nanoTime();
        return new ResultDto(flatResult, (finishTime - startTime) / 1_000_000.0);
    }

    private void initializeOpenCl() {
        if (isInitialized) {
            return;
        }
        isInitialized = true;
        final long deviceType = CL_DEVICE_TYPE_GPU;
        // Разрешить выбрасывать исключения
        CL.setExceptionsEnabled(true);

        // Obtain the number of platforms
        int[] numPlatformsArray = new int[1];
        clGetPlatformIDs(0, null, numPlatformsArray);
        int numPlatforms = numPlatformsArray[0];

        // ID платформы
        cl_platform_id[] platforms = new cl_platform_id[numPlatforms];
        clGetPlatformIDs(platforms.length, platforms, null);
        cl_platform_id platform = platforms[0];

        // Контекст платформы
        cl_context_properties contextProperties = new cl_context_properties();
        contextProperties.addProperty(CL_CONTEXT_PLATFORM, platform);

        // Чисто девайсов для платформы
        int[] numDevicesArray = new int[1];
        clGetDeviceIDs(platform, deviceType, 0, null, numDevicesArray);
        int numDevices = numDevicesArray[0];

        // ID девайса
        cl_device_id[] devices = new cl_device_id[numDevices];
        clGetDeviceIDs(platform, deviceType, numDevices, devices, null);
        cl_device_id device = devices[0];

        // Контекст девайса
        context = clCreateContext(
                contextProperties, 1, new cl_device_id[]{device},
                null, null, null
        );

        // Очередь для девайса
        cl_queue_properties properties = new cl_queue_properties();
        commandQueue = clCreateCommandQueueWithProperties(
                context, device, properties, null);

        // Исходный код kernel
        String source = getKernelSource();
        program = clCreateProgramWithSource(context,
                1, new String[]{source}, null, null
        );

        clBuildProgram(program, 0, null, null, null, null);
    }

    @Override
    public void release() {
        if (!isInitialized) {
            return;
        }
        clReleaseProgram(program);
        clReleaseCommandQueue(commandQueue);
        clReleaseContext(context);
    }

    private String getKernelSource() {
        try {
            return new String(Files.readAllBytes(Paths.get(GpuCalculator.class.getClassLoader().getResource(KERNEL_SOURCE).toURI())));
        } catch (IOException | URISyntaxException ex) {
            System.err.println("getKernelSource error");
            throw new RuntimeException(ex);
        }
    }

    @Override
    public String getName() {
        return "gpu_calculator";
    }
}
