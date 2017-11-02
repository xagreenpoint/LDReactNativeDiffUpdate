#
#  Be sure to run `pod spec lint react-native-diff-update.podspec' to ensure this is a
#  valid spec and to remove all comments including this before submitting the spec.
#
#  To learn more about Podspec attributes see http://docs.cocoapods.org/specification.html
#  To see working Podspecs in the CocoaPods repo see https://github.com/CocoaPods/Specs/
#

Pod::Spec.new do |s|

  s.name         = "LDRNDiffUpdate"
  s.version      = "1.0.4"
  s.summary      = "react native hot update."

  s.description  = <<-DESC
          react native component for diff update.
                   DESC

  s.homepage     = "https://github.com/xuwening/LDReactNativeDiffUpdate.git"
  # s.screenshots  = "www.example.com/screenshots_1.gif", "www.example.com/screenshots_2.gif"

  s.license          = { :type => 'MIT', :file => 'LICENSE' }
  s.author             = { "hongjingjun" => "hongjingjun@leadeon.cn" }

  # s.platform     = :ios
  # s.platform     = :ios, "5.0"
  s.ios.deployment_target = '8.0'

  s.library        = 'z', 'bz2'
  s.dependency 'React'
  s.dependency 'SSZipArchive'


  s.source_files = 'ios/LDRNDiffUpdate/*.{h,m}', 'ios/LDRNDiffUpdate/bsdiff/*.{h,c}', 'ios/LDRNDiffUpdate/md5/*.{h,m}'
  s.public_header_files = ['ios/LDRNDiffUpdate/LDRNDiffUpdate.h', 'ios/LDRNDiffUpdate/LDRNBundleList.h']

  # s.subspec 'Core' do |core|
  #   core.source_files = 'ios/LDRNDiffUpdate/*.{h,m}'
  #   core.public_header_files = ['ios/LDRNDiffUpdate/LDRNDiffUpdate.h']
  # end

  # s.subspec 'SSZipArchive' do |ss|
  #   ss.source_files = 'ios/LDRNDiffUpdate/SSZipArchive/*.{h,m}', 'ios/LDRNDiffUpdate/SSZipArchive/aes/*.{h,c}', 'ios/LDRNDiffUpdate/SSZipArchive/minizip/*.{h,c}'
  #   ss.private_header_files = 'ios/LDRNDiffUpdate/SSZipArchive/*.h', 'ios/LDRNDiffUpdate/SSZipArchive/aes/*.h', 'ios/LDRNDiffUpdate/SSZipArchive/minizip/*.h'
  # end

  # s.subspec 'bspatch' do |bs|
  #   bs.source_files = 'ios/LDRNDiffUpdate/bzip/*.{h,c}'
  #   bs.private_header_files = 'ios/LDRNDiffUpdate/bzip/*.h'
  # end

  # s.subspec 'md5' do |md|
  #   md.source_files = 'ios/LDRNDiffUpdate/md5/*.{h,m}'
  #   md.private_header_files = 'ios/LDRNDiffUpdate/md5/*.h'
  # end
  
  s.source       = { :git => "https://github.com/xuwening/LDReactNativeDiffUpdate.git", :tag => "#{s.version}" }

  # s.preserve_paths = "FilesToSave", "MoreFilesToSave"

  # s.framework  = "SomeFramework"
  # s.frameworks = "SomeFramework", "AnotherFramework"

  # s.library   = "iconv"
  # s.libraries = "iconv", "xml2"


end
