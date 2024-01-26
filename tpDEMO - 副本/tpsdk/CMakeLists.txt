cmake_minimum_required(VERSION 3.10.2)

#set(CMAKE_CXX_FLAGS "-Wno-error=format-security -Wno-error=pointer-sign")
#set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${PROJECT_SOURCE_DIR}/jniLibs/armeabi-v7a)
set(CMAKE_LIBRARY_OUTPUT_DIRECTORY src/main/jniLibs/${ANDROID_ABI})
#设置头⽂件搜索路径（和此txt同个路径的头⽂件⽆需设置），可选
#INCLUDE_DIRECTORIES(${CMAKE_CURRENT_SOURCE_DIR}/common)
#指定⽤到的系统库或者NDK库或者第三⽅库的搜索路径，可选。
#LINK_DIRECTORIES(/usr/local/lib)


message("tpsdk CMAKE_LIBRARY_OUTPUT_DIRECTORY"=${CMAKE_LIBRARY_OUTPUT_DIRECTORY})

#添加⼦⽬录，将⾃动找到⼦⽬录中的CMakeLists.txt
ADD_SUBDIRECTORY(${PROJECT_SOURCE_DIR}/src/main/cpp/opus)
#ADD_SUBDIRECTORY(${PROJECT_SOURCE_DIR}/src/main/cpp/decoder)