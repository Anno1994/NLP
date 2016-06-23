
#include <stdio.h>
#include <cuda.h>
#define N = 100

__global__ void dotproduct( float *a, float *b, float *c ) {
	const int threadsPerBlock = 100;
    __shared__ float cache[threadsPerBlock];
    int tid = threadIdx.x + blockIdx.x * blockDim.x;
    int cacheIndex = threadIdx.x;

    float acc = 0;
    while (tid < N) {
        acc += a[tid] * b[tid];
        tid += blockDim.x * gridDim.x;
    } 
    cache[cacheIndex] = acc;  
    __syncthreads(); // assure, that all threads in the block did their write

    int i = blockDim.x/2; // reduction: threadsPerBlock must be a power of 2
    while (i != 0) {
        if (cacheIndex < i)
            cache[cacheIndex] += cache[cacheIndex + i];
        __syncthreads();
        i /= 2;
    }

    if (cacheIndex == 0)
        c[blockIdx.x] = cache[0];
}

int main(void) {
	float *a1_h, *a2_h, *a3_h, *a1_d, *a2_d, *a3_d;
	const int N = 100;
	size_t size = N * sizeof(float);
	
	a1_h = (float *)malloc(size);
	a2_h = (float *)malloc(size);								
	a3_h = (float *)malloc(size);
	for (int i=0; i<N; i++) {
		a1_h[i] = (float)i;
		a2_h[i] = (float)i+5;
		a3_h[i] = (float)i*0.5;
	}
	
//	cudaEvent_t start, stop;
//	HANDLE_ERROR( cudaEventCreate( &start ) ); 
//	HANDLE_ERROR( cudaEventCreate( &stop ) ); 
//	HANDLE_ERROR( cudaEventRecord( start, 0 ) );

	cudaMalloc((void **)) &a1_d, size);	
	cudaMalloc((void **)) &a2_d, size);
	cudaMalloc((void **)) &a3_d, size);
	cudaMemcpy(a1_d, a1_h, size, cudaMemcpyHostToDevice);		
	cudaMemcpy(a2_d, a2_h, size, cudaMemcpyHostToDevice);		
	cudaMemcpy(a3_d, a3_h, size, cudaMemcpyHostToDevice);		
	
	int block_size = 4;
	int n_blocks = N/block_size + (N%block_size == 0 ? 0:1);
	dotproduct <<< n_blocks, block_size >>> (a1_d, a2_d, a3_d);
	free(a1_h, a2_h, a3_h); cudaFree(a1_d, a2_d, a3_d);

//	HANDLE_ERROR( cudaEventRecord( stop, 0 ) );
//	HANDLE_ERROR( cudaEventSynchronize( stop ) );
//	float   elapsedTime;
//	HANDLE_ERROR( cudaEventElapsedTime( &elapsedTime, start, stop ) );
//	printf( "Time for ...:  %3.1f ms\n", elapsedTime );
	
//	HANDLE_ERROR( cudaEventDestroy( start ) );
//	HANDLE_ERROR( cudaEventDestroy( stop ) );
}
