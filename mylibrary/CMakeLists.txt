

#工程路径
set(pathToProject D:/softWare/Android/ASProjects/BeautyDemo)
#OpenCV-android-sdk路径
set(pathToOpenCv D:/softWare/Android/OpenCV-3.2.0-android-sdk)

#CMake版本信息
cmake_minimum_required(VERSION 3.4.1)

#支持-std=gnu++11
set(CMAKE_VERBOSE_MAKEFILE on)
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++11")


#配置加载native依赖
include_directories(${pathToOpenCv}/sdk/native/jni/include)

#CPP文件夹下带编译的cpp文件
add_library(native-eyebrow  SHARED   src/main/cpp/native-eyebrow.cpp )
add_library(native-faceRec  SHARED   src/main/cpp/native-faceRec.cpp )
add_library(native-gestures SHARED   src/main/cpp/native-gestures.cpp )
add_library(native-glasses  SHARED   src/main/cpp/native-glasses.cpp )
add_library(native-pimple   SHARED   src/main/cpp/native-pimple.cpp )
add_library(native-skin     SHARED   src/main/cpp/native-skin.cpp )
add_library(native-wrinkle  SHARED   src/main/cpp/native-wrinkle.cpp )



#动态方式加载
#添加预编译库和添加本地库（native library）类似。
#由于预编译库是已经构建好的，你想就要使用 IMPORTED 标志去告诉 CMake ，你只需要将其导入到你的项目中即可：
add_library( lib_opencv SHARED IMPORTED )

#然后你需要使用 set_target_properties() 命令去指定库的路径
#引入libopencv_java3.so文件
set_target_properties(lib_opencv PROPERTIES IMPORTED_LOCATION ${pathToProject}/mylibrary/src/main/jniLibs/${ANDROID_ABI}/libopencv_java3.so)

#C++日志
find_library( log-lib log )

#在 CMake 构建脚本中使用 target_link_libraries() 命令，将预构建库与你本地库相关联：
target_link_libraries( native-eyebrow   lib_opencv ${log-lib} )
target_link_libraries( native-faceRec   lib_opencv ${log-lib} )
target_link_libraries( native-gestures  lib_opencv ${log-lib} )
target_link_libraries( native-glasses   lib_opencv ${log-lib} )
target_link_libraries( native-skin      lib_opencv ${log-lib} )
target_link_libraries( native-wrinkle   lib_opencv ${log-lib} )
target_link_libraries( native-pimple    lib_opencv ${log-lib} )
