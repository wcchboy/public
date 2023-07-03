#继承上⼀层的CMakeLists.txt的变量，也可以在这⾥重新赋值
#C 的编译选项是 CMAKE_C_FLAGS
# 指定编译参数，可选
#SET(CMAKE_CXX_FLAGS "-Wno-error=format-security -Wno-error=pointer-sign")
set(libs_DIR ${PROJECT_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI})

#set(libs_DIR ${PROJECT_SOURCE_DIR}/src/main/jniLibs/armeabi-v7a)
#set(libs_DIR ${PROJECT_SOURCE_DIR}/src/main/jniLibs/armeabi-v7a)

message("libs_DIR"=${libs_DIR})
set(libs_include_DIR include)

include_directories(${libs_include_DIR})
link_directories(${libs_DIR})


#⽣成动态库名称、类型、资源⽂件
add_library(opusJni SHARED opus-lib.cpp)
#依赖库
find_library(log-lib log)
#
target_link_libraries( opusJni opus ${log-lib} )