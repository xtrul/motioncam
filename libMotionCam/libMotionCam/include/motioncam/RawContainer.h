#ifndef RawContainer_hpp
#define RawContainer_hpp

#include <string>
#include <set>
#include <map>

#include <opencv2/opencv.hpp>
#include <json11/json11.hpp>

#include "motioncam/RawImageMetadata.h"

namespace motioncam {
    namespace util {
        class ZipWriter;
        class ZipReader;
    }

    class RawContainer {
    public:
        RawContainer(const std::string& inputPath);
        RawContainer(const RawCameraMetadata& cameraMetadata);

        RawContainer(const RawCameraMetadata& cameraMetadata,
                     const PostProcessSettings& postProcessSettings,
                     const int64_t referenceTimestamp,
                     const bool isHdr,
                     const std::vector<std::shared_ptr<RawImageBuffer>>& buffers);

        const RawCameraMetadata& getCameraMetadata() const;
        const PostProcessSettings& getPostProcessSettings() const;

        std::string getReferenceImage() const;
        void updateReferenceImage(const std::string& referenceName);
        
        bool isHdr() const;
        std::vector<std::string> getFrames() const;
        
        std::shared_ptr<RawImageBuffer> getFrame(const std::string& frame) const;
        std::shared_ptr<RawImageBuffer> loadFrame(const std::string& frame) const;
        void removeFrame(const std::string& frame);
        
        void save(const std::string& outputPath);
        
        static size_t append(util::ZipWriter& zipWriter, std::shared_ptr<RawImageBuffer> frame);
        
        bool isInMemory() const { return mIsInMemory; };
        
    private:
        void initialise();
        
        static std::string getRequiredSettingAsString(const json11::Json& json, const std::string& key);
        static int getRequiredSettingAsInt(const json11::Json& json, const std::string& key);
        static std::string getOptionalStringSetting(const json11::Json& json, const std::string& key, const std::string& defaultValue);
        static int getOptionalSetting(const json11::Json& json, const std::string& key, const int defaultValue);
        static bool getOptionalSetting(const json11::Json& json, const std::string& key, const bool defaultValue);
    
        static std::string toString(ColorFilterArrangment sensorArrangment);
        static std::string toString(PixelFormat format);
        static std::string toString(RawType rawType);
        
        static cv::Mat toMat3x3(const std::vector<json11::Json>& array);
        static cv::Vec3f toVec3f(const std::vector<json11::Json>& array);
        static json11::Json::array toJsonArray(cv::Mat m);

        static std::shared_ptr<RawImageBuffer> loadFrameMetadata(const json11::Json& obj);
        static void generateMetadata(std::shared_ptr<RawImageBuffer> frame, json11::Json::object& metadata, const std::string& filename);

    private:
        std::unique_ptr<util::ZipReader> mZipReader;
        RawCameraMetadata mCameraMetadata;
        PostProcessSettings mPostProcessSettings;
        int64_t mReferenceTimestamp;
        std::string mReferenceImage;
        bool mIsHdr;
        bool mIsInMemory;
        std::vector<std::string> mFrames;
        std::map<std::string, std::shared_ptr<RawImageBuffer>> mFrameBuffers;
    };
}

#endif /* RawContainer_hpp */
