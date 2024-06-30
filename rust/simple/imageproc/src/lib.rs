// Remove the unused import
use std::slice;
#[cfg(target_os = "android")]
use android_logger::{Config, FilterBuilder};
#[cfg(target_os = "android")]
use log::{info, error, LevelFilter};


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
fn init_logger() {
    android_logger::init_once(
        Config::default()
        .with_max_level(LevelFilter::Trace) // Adjust log level as needed
        .with_tag("imageproc") // Custom tag for logcat
        .with_filter(
            FilterBuilder::new()
                .parse("debug,imageproc=info")
                .build()
        ),
    );
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
    // let input_bytes = env.get_direct_buffer_address(&input).unwrap();
    // process_image(input_bytes, width, height);
    init_logger();
    info!("Processing image with width: {} and height: {}", width, height);
    let size = (width * height * 3) as usize; // Assuming RGB images
    if input.is_null() {
        error!("Input buffer is null");
        return;
    }
    let input_bytes = match env.get_direct_buffer_address(&input) {
        Ok(ptr) => ptr,
        Err(_) => {
            error!("Failed to get buffer address");
            return;
        }
    };

    let buffer = unsafe { slice::from_raw_parts_mut(input_bytes, size) };
    if size > buffer.len() {
        error!("Buffer size mismatch: expected at least {}, got {}", size, buffer.len());
        return;
    }


    info!("Starting to process buffer of size: {}", size);
    info!("Buffer address: {:p}, size: {}", input_bytes, size);

// Use get and get_mut for safer access
// for i in 0..size {
//     if let Some(pixel) = buffer.get_mut(i) {
//         *pixel = 255 - *pixel;
//     } else {
//         error!("Index out of bounds: {}", i);
//         break;
//     }
// }

    info!("Finished processing buffer");
}
