/**
 * OpenCL Sample
 * 2019-10-01 K.OHWADA
 * original : https://github.com/wf9a5m75/opencl_test
 */

#include "helloworld.h"
#include "hello_cl.h"

/**
 * helloworld
 */
int helloworld(char* result)
{
  cl_device_id device_id = NULL;
  cl_context context = NULL;
  cl_command_queue command_queue = NULL;
  cl_mem memobj = NULL;
  cl_program program = NULL;
  cl_kernel kernel = NULL;
  cl_platform_id platform_id = NULL;
  cl_uint ret_num_devices;
  cl_uint ret_num_platforms;
  cl_int ret;

  size_t source_size;

    // load source code including kernel
  source_size = sizeof(HELLO_CL);


  /*
   *   Get platform / device information
   * プラットフォーム・デバイスの情報取得
   *   OpenCLが動作するプラットフォーム(=ハードウェア)を認識して
   *   該当するプラットフォームを第2引数(platform_id)に返す。
   *   以降はこのplatform_idを参照することで、プラットフォームが利用できる
   *
   * args[0] = ホスト側のアプリケーションが望むプラットフォームの数(通常は1)
   * args[1] = プラットフォームIDが返される
   * args[2] = 実際に利用できるOpenCL対応のプラットフォームの数が返される
   */
  ret = clGetPlatformIDs(1, &platform_id, &ret_num_platforms);
  if (ret == CL_SUCCESS) {
    LOGE("--->clGetPlatformIDs = success");
    LOGE("       ret_num_platforms = %d", ret_num_platforms);
    LOGE("       platform_id = 0x%08x", (int)platform_id);
  } else {
    print_error("clGetPlatformIDs", ret);
    return -1;
  }

  /*
   * Device identification
   * デバイスの特定
   *   プラットフォームで使うGPUといったデバイスを特定する。
   *
   * args[0] = プラットフォームID
   * args[1] = デバイスの種類を指定
   *   CL_DEVICE_TYPE_DEFAULT : 標準デバイス
   *   CL_DEVICE_TYPE_GPU : 明示的にGPU
   *   CL_DEVICE_TYPE_CPU : ホスト側のCPUをデバイスとして使用
   * args[2] = 利用したいデバイスの数
   * args[3] = デバイスIDが返される
   * args[4] = args[1]で指定したデバイスの数か返される。利用できるデバイスがないときは0
   */
  ret = clGetDeviceIDs(platform_id, CL_DEVICE_TYPE_DEFAULT, 1, &device_id, &ret_num_devices);
  if (ret == CL_SUCCESS) {
    LOGE("--->clGetDeviceIDs = success");
  } else {
    print_error("clGetDeviceIDs", ret);
    return -1;
  }

  /*
   * Creating an OpenCL context
   * OpenCLコンテキストの作成
   *   OpenCLを動作させる実行環境となるOpenCLコンテキストを作成する。
   *   以降作成する各種OpenCLのオブジェクトは、このコンテキストに所属することになり、
   *   同一コンテキスト内のオブジェクトを通して各デバイスを制御できる。
   *   このコンテキストは演算デバイスが1つ以上利用できる1つの仮想コンピュータと捉えると良いらしい。
   *
   * args[0] = ?
   * args[1] = 利用するデバイスの数
   * args[2] = 利用するデバイスに対応するデバイスハンドルのリスト
   * args[3] = ?
   * args[4] = ?
   * args[5] = 戻り値
   *
   * ret = OpenCLコンテキスト
   */
  context =  clCreateContext(NULL, 1, &device_id, NULL, NULL, &ret);
  if (ret == CL_SUCCESS) {
    LOGE("--->clCreateContext = success");
  } else {
    print_error("clGetDeviceIDs", ret);
    return -1;
  }

  /*
   * Creating a command queue
   * コマンドキューの作成
   *   OpenCLではコマンドキューを通して、ホストからデバイスに対する働きかけ
   *   (カーネル実行コマンドや、ホストーデバイス間のメモリコピーコマンド）を
   *   実行する。1つのデバイスに対して必ず1つ以上のコマンドキューを作成する。
   *
   * args[0] = コンテキスト
   * args[1] = デバイスID
   * args[2] = ?
   * args[3] = 戻り値
   *
   * ret = コマンドキュー
   */
  command_queue = clCreateCommandQueue(context, device_id, 0, &ret);
  if (ret == CL_SUCCESS) {
    LOGE("--->clCreateCommandQueue = success");
  } else {
    print_error("clCreateCommandQueue", ret);
    return -1;
  }

  /*
   * Creating a memory object
   * メモリオブジェクトの作成
   *   デバイス側のメモリにホスト側からアクセスするためのメモリオブジェクトの作成。
   *   デバイスはデバイス内のメモリにしかアクセスできないので、ホスト側が
   *   デバイス内のメモリに必要なデータを設定する。
   *
   * args[0] = コンテキスト
   * args[1] = メモリ属性
   * args[2] = 確保するメモリ容量
   * args[3] = ?
   * args[4] = 戻り値
   *
   * ret = メモリオブジェクト
   */
  memobj = clCreateBuffer(context, CL_MEM_READ_WRITE, MEM_SIZE * sizeof(char), NULL, &ret);
  if (ret == CL_SUCCESS) {
    LOGE("--->clCreateBuffer = success");
  } else {
    print_error("clCreateBuffer", ret);
    return -1;
  }

  /*
   * Create a kernel program from the loaded source code
   * 読み込んだソースコードからカーネルプログラムを作成
   *   OpenCLではカーネルプログラムをまずプログラムオブジェクトとして認識する。
   *   コンパイル済バイナリから作成する場合は、clCreateProgramWithBinary()を利用する。
   *
   * args[0] = コンテキスト
   * args[1] = ?
   * args[2] = カーネルプログラムのソースコード
   * args[3] = 同、バイト数
   */
  program = clCreateProgramWithSource(context, 1, &HELLO_CL, NULL, &ret);
  if (ret == CL_SUCCESS) {
    LOGE("--->clCreateProgramWithSource = success");
  } else {
    print_error("clCreateProgramWithSource", ret);
    return -1;
  }

  /*
   * Build kernel program
   * カーネルプログラムをビルド
   *   プログラムオブジェクトをOpenCL Cコンパイラ・リンカを使用してビルド。
   *
   * args[0] = プログラムオブジェクト
   * args[1] = args[2]で指定するデバイスの数
   * args[2] = デバイスリストへのポインタ
   * args[3] = コンパイラオプションの文字列
   * args[4] = ?
   * args[5] = ?
   */
  ret = clBuildProgram(program, 1, &device_id, NULL, NULL, NULL);
  if (ret == CL_SUCCESS) {
    LOGE("--->clBuildProgram = success");
  } else if (ret == CL_DEVICE_NOT_FOUND) {
    print_error("clBuildProgram", ret);
    return -1;
  }

  /*
   * Creating an OpenCL kernel
   * OpenCLカーネルの作成
   *   カーネルオブジェクトの作成。
   *   1つのカーネルオブジェクトは1つのカーネル関数に対応するので
   *   作成時に関数名を作成する。
   *
   *   複数のカーネル関数を1つにプログラムオブジェクトとして記述してもOK。
   *   ただし1つのカーネルオブジェクトは1つのカーネル関数に1対1なので、clCreateKernel()を複数回呼び出すことになる。
   *
   * args[0] = プログラムオブジェクト
   * args[1] = 関数名
   * args[2] = 戻り値
   *
   * ret = カーネルオブジェクト
   */
  kernel = clCreateKernel(program, "hello", &ret);
  if (ret == CL_SUCCESS) {
    LOGE("--->clCreateKernel = success");
  } else {
    print_error("clCreateKernel", ret);
    return -1;
  }

  /*
   * Setting OpenCL kernel arguments
   * OpenCLカーネル引数の設定
   *   hello()関数のargs[0] stringがどこにあるのかを指定する。
   *   __global付きカーネル引数に対しては、
   *   ホスト側から確保したデバイスメモリを示すメモリオブジェクトを割り当てる。
   *
   * args[0] = カーネルオブジェクト
   * args[1] = カーネル引数の位置
   * args[2] = args[3]のサイズ
   * args[3] = 与える値(この例ではメモリオブジェクト)
   *
   * ----------------------
   * ホスト側のデータをカーネル引数として直接値渡しする場合の例
   *   int a = 10;
   *   clSetKernelArg(kernel, 0, sizeof(int), (void *)&a);
   */
  ret = clSetKernelArg(kernel, 0, sizeof(cl_mem), (void *)&memobj);
  if (ret == CL_SUCCESS) {
    LOGE("--->clSetKernelArg = success");
  } else {
    print_error("clSetKernelArg", ret);
    return -1;
  }

  /*
   * Run OpenCL kernel
   * OpenCLカーネルを実行(コマンドキューに投入)
   *   デバイスにカーネル関数hello()を実行するようにリクエストする。
   *   この関数が終了した時点ではコマンドキューに投入されただけで
   *   実行が完了したことは保証されない。
   *   完了を待つためには、args[4]でイベントオブジェクトを取得する必要があるが
   *   HelloWorldの時点では紹介されていないので、省略。
   *
   * args[0] = コマンドキュー
   * args[1] = 投入するカーネル
   * args[2] = ?
   * args[3] = ?
   * args[4] = ?
   */
  ret = clEnqueueTask(command_queue, kernel, 0, NULL, NULL);
  if (ret == CL_SUCCESS) {
    LOGE("--->clEnqueueTask = success");
  } else {
    print_error("clEnqueueTask", ret);
    return -1;
  }

  /*
   * Get result through memory object
   * メモリオブジェクトを通じて結果を取得
   *   デバイス側のメモリからホスト側のメモリにデータをコピーする。
   *   (逆の場合はclEnqueueWriteBuffer())
   *
   *   Enqueueと名前にある通り、キューを通じて実行される
   *
   * args[0] = コマンドキュー
   * args[1] = デバイス側のメモリ
   * args[2] = データ完了まで待つかどうか
   *   CL_TRUE : 待つ(同期コピー)
   *   CL_FALSE : 待たない(非同期コピー)
   * args[3] = args[4]のコピーするサイズ
   * args[4] = コピー先(ホスト側のメモリのポインタ)
   * args[5] = オフセット?
   * args[6] = ?
   * args[7] = ?
   */
  ret = clEnqueueReadBuffer(command_queue, memobj, CL_TRUE, 0, MEM_SIZE * sizeof(char), result, 0, NULL, NULL);
  if (ret == CL_SUCCESS) {
    LOGE("--->clEnqueueReadBuffer = success");
  } else {
    print_error("clEnqueueReadBuffer", ret);
    return -1;
  }

  /*
   * results to log
   */
  LOGD("answer = %s", result);

  /*
   * Final processing
   */
  ret = clFlush(command_queue);
  ret = clFinish(command_queue);
  ret = clReleaseKernel(kernel);
  ret = clReleaseProgram(program);
  ret = clReleaseMemObject(memobj);
  ret = clReleaseCommandQueue(command_queue);
  ret = clReleaseContext(context);

    return 0;
}


/**
  * print_error
 */
void print_error(char *name, cl_int ret) {
  if (ret == CL_DEVICE_NOT_FOUND) {
    LOGE("--->%s = CL_DEVICE_NOT_FOUND", name);
  } else if (ret == CL_DEVICE_NOT_AVAILABLE) {
    LOGE("--->%s = CL_DEVICE_NOT_AVAILABLE", name);
  } else if (ret == CL_COMPILER_NOT_AVAILABLE) {
    LOGE("--->%s = CL_COMPILER_NOT_AVAILABLE", name);
  } else if (ret == CL_MEM_OBJECT_ALLOCATION_FAILURE) {
    LOGE("--->%s = CL_MEM_OBJECT_ALLOCATION_FAILURE", name);
  } else if (ret == CL_OUT_OF_RESOURCES) {
    LOGE("--->%s = CL_OUT_OF_RESOURCES", name);
  } else if (ret == CL_OUT_OF_HOST_MEMORY) {
    LOGE("--->%s = CL_OUT_OF_HOST_MEMORY", name);
  } else if (ret == CL_PROFILING_INFO_NOT_AVAILABLE) {
    LOGE("--->%s = CL_PROFILING_INFO_NOT_AVAILABLE", name);
  } else if (ret == CL_MEM_COPY_OVERLAP) {
    LOGE("--->%s = CL_MEM_COPY_OVERLAP", name);
  } else if (ret == CL_IMAGE_FORMAT_MISMATCH) {
    LOGE("--->%s = CL_IMAGE_FORMAT_MISMATCH", name);
  } else if (ret == CL_IMAGE_FORMAT_NOT_SUPPORTED) {
    LOGE("--->%s = CL_IMAGE_FORMAT_NOT_SUPPORTED", name);
  } else if (ret == CL_BUILD_PROGRAM_FAILURE) {
    LOGE("--->%s = CL_BUILD_PROGRAM_FAILURE", name);
  } else if (ret == CL_MAP_FAILURE) {
    LOGE("--->%s = CL_MAP_FAILURE", name);
  } else if (ret == CL_MISALIGNED_SUB_BUFFER_OFFSET) {
    LOGE("--->%s = CL_MISALIGNED_SUB_BUFFER_OFFSET", name);
  } else if (ret == CL_EXEC_STATUS_ERROR_FOR_EVENTS_IN_WAIT_LIST) {
    LOGE("--->%s = CL_EXEC_STATUS_ERROR_FOR_EVENTS_IN_WAIT_LIST", name);
  } else if (ret == CL_INVALID_VALUE) {
    LOGE("--->%s = CL_INVALID_VALUE", name);
  } else if (ret == CL_INVALID_DEVICE_TYPE) {
    LOGE("--->%s = CL_INVALID_DEVICE_TYPE", name);
  } else if (ret == CL_INVALID_PLATFORM) {
    LOGE("--->%s = CL_INVALID_PLATFORM", name);
  } else if (ret == CL_INVALID_DEVICE) {
    LOGE("--->%s = CL_INVALID_DEVICE", name);
  } else if (ret == CL_INVALID_CONTEXT) {
    LOGE("--->%s = CL_INVALID_CONTEXT", name);
  } else if (ret == CL_INVALID_QUEUE_PROPERTIES) {
    LOGE("--->%s = CL_INVALID_QUEUE_PROPERTIES", name);
  } else if (ret == CL_INVALID_COMMAND_QUEUE) {
    LOGE("--->%s = CL_INVALID_COMMAND_QUEUE", name);
  } else if (ret == CL_INVALID_HOST_PTR) {
    LOGE("--->%s = CL_INVALID_HOST_PTR", name);
  } else if (ret == CL_INVALID_MEM_OBJECT) {
    LOGE("--->%s = CL_INVALID_MEM_OBJECT", name);
  } else if (ret == CL_INVALID_IMAGE_FORMAT_DESCRIPTOR) {
    LOGE("--->%s = CL_INVALID_IMAGE_FORMAT_DESCRIPTOR", name);
  } else if (ret == CL_INVALID_IMAGE_SIZE) {
    LOGE("--->%s = CL_INVALID_IMAGE_SIZE", name);
  } else if (ret == CL_INVALID_SAMPLER) {
    LOGE("--->%s = CL_INVALID_SAMPLER", name);
  } else if (ret == CL_INVALID_BINARY) {
    LOGE("--->%s = CL_INVALID_BINARY", name);
  } else if (ret == CL_INVALID_BUILD_OPTIONS) {
    LOGE("--->%s = CL_INVALID_BUILD_OPTIONS", name);
  } else if (ret == CL_INVALID_PROGRAM) {
    LOGE("--->%s = CL_INVALID_PROGRAM", name);
  } else if (ret == CL_INVALID_PROGRAM_EXECUTABLE) {
    LOGE("--->%s = CL_INVALID_PROGRAM_EXECUTABLE", name);
  } else if (ret == CL_INVALID_KERNEL_NAME) {
    LOGE("--->%s = CL_INVALID_KERNEL_NAME", name);
  } else if (ret == CL_INVALID_KERNEL_DEFINITION) {
    LOGE("--->%s = CL_INVALID_KERNEL_DEFINITION", name);
  } else if (ret == CL_INVALID_KERNEL) {
    LOGE("--->%s = CL_INVALID_KERNEL", name);
  } else if (ret == CL_INVALID_ARG_INDEX) {
    LOGE("--->%s = CL_INVALID_ARG_INDEX", name);
  } else if (ret == CL_INVALID_ARG_VALUE) {
    LOGE("--->%s = CL_INVALID_ARG_VALUE", name);
  } else if (ret == CL_INVALID_ARG_SIZE) {
    LOGE("--->%s = CL_INVALID_ARG_SIZE", name);
  } else if (ret == CL_INVALID_KERNEL_ARGS) {
    LOGE("--->%s = CL_INVALID_KERNEL_ARGS", name);
  } else if (ret == CL_INVALID_WORK_DIMENSION) {
    LOGE("--->%s = CL_INVALID_WORK_DIMENSION", name);
  } else if (ret == CL_INVALID_WORK_GROUP_SIZE) {
    LOGE("--->%s = CL_INVALID_WORK_GROUP_SIZE", name);
  } else if (ret == CL_INVALID_WORK_ITEM_SIZE) {
    LOGE("--->%s = CL_INVALID_WORK_ITEM_SIZE", name);
  } else if (ret == CL_INVALID_GLOBAL_OFFSET) {
    LOGE("--->%s = CL_INVALID_GLOBAL_OFFSET", name);
  } else if (ret == CL_INVALID_EVENT_WAIT_LIST) {
    LOGE("--->%s = CL_INVALID_EVENT_WAIT_LIST", name);
  } else if (ret == CL_INVALID_EVENT) {
    LOGE("--->%s = CL_INVALID_EVENT", name);
  } else if (ret == CL_INVALID_OPERATION) {
    LOGE("--->%s = CL_INVALID_OPERATION", name);
  } else if (ret == CL_INVALID_GL_OBJECT) {
    LOGE("--->%s = CL_INVALID_GL_OBJECT", name);
  } else if (ret == CL_INVALID_BUFFER_SIZE) {
    LOGE("--->%s = CL_INVALID_BUFFER_SIZE", name);
  } else if (ret == CL_INVALID_MIP_LEVEL) {
    LOGE("--->%s = CL_INVALID_MIP_LEVEL", name);
  } else if (ret == CL_INVALID_GLOBAL_WORK_SIZE) {
    LOGE("--->%s = CL_INVALID_GLOBAL_WORK_SIZE", name);
  } else if (ret == CL_INVALID_PROPERTY) {
    LOGE("--->%s = CL_INVALID_PROPERTY", name);
  } else {
    LOGE("--->%s is failed", name);
  }
}