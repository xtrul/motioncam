#ifndef MotionCam_hpp
#define MotionCam_hpp

#include <string>

#include "motioncam/ImageProcessorProgress.h"
#include "motioncam/DngProcessorProgress.h"

namespace motioncam {
    class RawContainer;

    float ConvertVideoToDNG(const std::string& containerPath, const DngProcessorProgress& progress, const int numThreads=4);

    void ProcessImage(RawContainer& rawContainer, const std::string& outputFilePath, const ImageProcessorProgress& progressListener);
    void ProcessImage(const std::string& containerPath, const std::string& outputFilePath, const ImageProcessorProgress& progressListener);
}

#endif /* MotionCam_hpp */
