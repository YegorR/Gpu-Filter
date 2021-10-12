__kernel void mykernel(__global const float *image, uint image_width,
							__global const float *gauss, uint gauss_width,
							__global float *result)	{
		int i = get_global_id(0);
		int j = get_global_id(1);
		
		float local_result = 0.0f;
		
		for (int ii = 0; ii < gauss_width; ++ii) {
			for (int jj = 0; jj < gauss_width; ++jj) {
				local_result += image[(i + ii) * image_width + (j + jj)] * gauss[ii * gauss_width + jj];
			}
		}
		result[i * (image_width - gauss_width + 1) + j] = local_result;
}
