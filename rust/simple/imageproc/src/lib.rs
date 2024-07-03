// Remove the unused import
#[cfg(target_os = "android")]
use android_logger::{Config, FilterBuilder};
#[cfg(target_os = "android")]
use log::{error, info, LevelFilter};
use std::alloc::{alloc, dealloc, Layout};
use std::slice;


#[cfg(target_os = "android")]
extern crate jni;
#[cfg(target_os = "android")]
use jni::objects::{JByteBuffer, JClass};
#[cfg(target_os = "android")]
use jni::sys::{jint, jobject};
#[cfg(target_os = "android")]
use jni::JNIEnv;

#[no_mangle]
pub extern "C" fn process_image(input: *mut u8, width: i32, height: i32) {
    let size = (width * height * 4) as usize; // Adjusted for RGBA images
    let buffer = unsafe { slice::from_raw_parts_mut(input, size) };

    // Example processing: Invert colors
    for i in (0..size).step_by(4) {
        // Invert R, G, B values
        buffer[i] = 255 - buffer[i]; // R
        buffer[i + 1] = 255 - buffer[i + 1]; // G
        buffer[i + 2] = 255 - buffer[i + 2]; // B
                                             // Alpha channel remains unchanged
                                             // buffer[i + 3] is the alpha component and is not modified
    }
}

#[cfg(target_os = "android")]
fn init_logger() {
    android_logger::init_once(
        Config::default()
            .with_max_level(LevelFilter::Trace) // Adjust log level as needed
            .with_tag("imageproc") // Custom tag for logcat
            .with_filter(FilterBuilder::new().parse("debug,imageproc=info").build()),
    );
}

#[cfg(target_os = "android")]
#[no_mangle]
pub extern "C" fn Java_ai_skydom_simcam_NativeLib_procimage(
    mut env: JNIEnv,
    class: JClass,
    input: JByteBuffer,
    rotation_degrees: i32,
    width: i32,
    height: i32,
) -> jobject {
    // Assuming you might want to return a new Java byte array
    // let input_bytes = env.get_direct_buffer_address(&input).unwrap();

    fn log_pixel_values(rgb_buffer: *const u8, width: i32, height: i32) {
        let positions = [(0, 0), (10, 10), (width - 1, height - 1)]; // Example positions: top-left, middle, bottom-right
        for (x, y) in positions.iter() {
            let index = (y * width + x) * 4; // Calculate the byte index for pixel (x, y)
            unsafe {
                let r = *rgb_buffer.add(index as usize + 0);
                let g = *rgb_buffer.add(index as usize + 1);
                let b = *rgb_buffer.add(index as usize + 2);
                let a = *rgb_buffer.add(index as usize + 3);
                info!("Pixel at ({}, {}): ARGB({}, {}, {}, {})", x, y, a, r, g, b);
            }
        }
    }

    init_logger();
    info!(
        "Processing image with width: {} and height: {}",
        width, height
    );

    if input.is_null() {
        error!("Input buffer is null");
        return std::ptr::null_mut();
    }

    let input_bytes = match env.get_direct_buffer_address(&input) {
        Ok(buf) => buf,
        Err(e) => {
            error!("Failed to get buffer address: {:?}", e);
            return std::ptr::null_mut();
        }
    };
    let size = (width * height * 4) as usize; // Assuming aRGB images

    log_pixel_values(input_bytes, width, height);



    info!("Starting to process buffer of size: {}", size);

    process_image(input_bytes, width, height);

    log_pixel_values(input_bytes, width, height);

    info!("Finished processing buffer");
    // Create a ByteBuffer from the Rust-allocated RGB buffer to return to Java
    let byte_buffer_obj = unsafe { env.new_direct_byte_buffer(input_bytes, size) };
    match byte_buffer_obj {
        Ok(bb) => **bb,
        Err(e) => {
            error!("Failed to create a direct ByteBuffer: {:?}", e);
            std::ptr::null_mut()
        }
    }
}

