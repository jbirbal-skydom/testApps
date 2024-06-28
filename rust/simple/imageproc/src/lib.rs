// Remove the unused import
use std::slice;

#[cfg(target_os = "android")]
extern crate jni;
#[cfg(target_os = "android")]
use jni::JNIEnv;
#[cfg(target_os = "android")]
use jni::objects::{JClass, JByteBuffer};

#[no_mangle]
pub extern "C" fn process_image(input: *mut u8, width: i32, height: i32) {
    let size = (width * height * 3) as usize; // Assuming RGB images
    let buffer = unsafe { slice::from_raw_parts_mut(input, size) };

    // Example processing: Invert colors
    for pixel in buffer.iter_mut() {
        *pixel = 255 - *pixel;
    }
}

#[cfg(target_os = "android")]
#[no_mangle]
pub extern "C" fn Java_ai_skydom_simcam_NativeLib_procimage(
    env: JNIEnv, 
    class: JClass, 
    input: JByteBuffer, 
    width: i32, 
    height: i32
) {
    let input_bytes = env.get_direct_buffer_address(&input).unwrap();
    process_image(input_bytes, width, height);
}
