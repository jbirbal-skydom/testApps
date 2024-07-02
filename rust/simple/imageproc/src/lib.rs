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
    let size = (width * height * 3) as usize; // Assuming RGB images
    let buffer = unsafe { slice::from_raw_parts_mut(input, size) };

    // Example processing: Invert colors
    for pixel in buffer.iter_mut() {
        *pixel = 255 - *pixel;
    }
}

// Example placeholder for YUV to RGB conversion function
/// Converts YUV to ARGB and writes the ARGB data into the provided buffer.
// pub fn convert_yuv_to_argb(y_data: *const u8, u_data: *const u8, v_data: *const u8, rgba_buffer: *mut u8, width: i32, height: i32) {
//     let num_pixels: usize = (width * height) as usize;
//     let y_plane = unsafe { std::slice::from_raw_parts(y_data, num_pixels) };
//     let u_plane = unsafe { std::slice::from_raw_parts(u_data, num_pixels / 4) }; // Assuming subsampling
//     let v_plane = unsafe { std::slice::from_raw_parts(v_data, num_pixels / 4) }; // Assuming subsampling

//     for y in 0..height {
//         for x in 0..width {
//             let y_index = (y * width + x) as usize;
//             let uv_index = (y / 2) * (width / 2) + (x / 2);

//             let y_val = y_plane[y_index] as f32;
//             let u_val = u_plane[uv_index as usize] as f32 - 128.0;
//             let v_val = v_plane[uv_index as usize] as f32 - 128.0;

//             let r = (y_val + 1.402 * v_val).max(0.0).min(255.0) as u8;
//             let g = (y_val - 0.344136 * u_val - 0.714136 * v_val).max(0.0).min(255.0) as u8;
//             let b = (y_val + 1.772 * u_val).max(0.0).min(255.0) as u8;
//             let a = 255; // Alpha channel is fully opaque

//             let argb_index = y_index * 4;
//             unsafe { *rgba_buffer.add(argb_index) = a };
//             unsafe { *rgba_buffer.add(argb_index + 1) = r };
//             unsafe { *rgba_buffer.add(argb_index + 2) = g };
//             unsafe { *rgba_buffer.add(argb_index + 3) = b };
//         }
//     }
// }

#[cfg(target_os = "android")]
fn init_logger() {
    android_logger::init_once(
        Config::default()
            .with_max_level(LevelFilter::Trace) // Adjust log level as needed
            .with_tag("imageproc") // Custom tag for logcat
            .with_filter(FilterBuilder::new().parse("debug,imageproc=info").build()),
    );
}

pub fn rotate_image(data: &mut [u8], width: usize, height: usize, degrees: i32) {
    // Example: rotate 90 degrees clockwise
    let mut new_data = vec![0; data.len()]; // New image data storage
    for y in 0..height {
        for x in 0..width {
            let new_x = height - 1 - y;
            let new_y = x;
            let index = (y * width + x) * 4;
            let new_index = (new_y * height + new_x) * 4;
            new_data[new_index..new_index + 4].copy_from_slice(&data[index..index + 4]);
            // Assuming RGBA
        }
    }
    data.copy_from_slice(&new_data);
}

#[cfg(target_os = "android")]
#[no_mangle]
pub extern "C" fn Java_ai_skydom_simcam_NativeLib_procimage(
    mut env: JNIEnv,
    class: JClass,
    input: JByteBuffer,
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

    // process_image(input_bytes, width, height);
    init_logger();
    info!(
        "Processing image with width: {} and height: {}",
        width, height
    );

    let size = (width * height * 4) as usize; // Assuming aRGB images
                                              // if input.is_null() {
                                              //     error!("Input buffer is null");
                                              //     return;
                                              // }
                                              // let input_bytes: *mut u8 = env.get_direct_buffer_address(&input).unwrap();
                                              // let buffer = unsafe { slice::from_raw_parts_mut(input_bytes, size) };

    // Allocate new buffer for RGB output
    let num_pixels = (width * height) as usize;
    let rgba_layout = Layout::array::<u8>(num_pixels * 4).unwrap();
    let rgba_buffer = unsafe { alloc(rgba_layout) };
    let rgba_buffer = unsafe { alloc(rgba_layout) };
    if rgba_buffer.is_null() {
        error!("Failed to allocate memory for RGB buffer");
        return std::ptr::null_mut(); // Ensure to return properly if allocation fails
    }

    info!(
        "Created image  buffer size: {} and location: {:?}",
        num_pixels, rgba_buffer
    );
    log_pixel_values(rgba_buffer, width, height);

    info!("Starting to process buffer of size: {}", size);

    // Assuming RGBA interate over 4 bytes and change the alpha value for each pixel to 255
    for y in 0..height {
        for x in 0..width {
            let index = (y * width + x) * 4; // Calculate the byte index for pixel (x, y)
            unsafe {
                *rgba_buffer.add(index as usize + 3 ) = 255;  // Assuming the first byte is the alpha channel
            }
        }
    }

    if rgba_buffer.is_null() {
        // Handle allocation failure
        error!("Failed to allocate memory for RGB buffer");
    }

    log_pixel_values(rgba_buffer, width, height);

    info!("Finished processing buffer");
    // Create a ByteBuffer from the Rust-allocated RGB buffer to return to Java
    let byte_buffer_obj = unsafe { env.new_direct_byte_buffer(rgba_buffer, num_pixels * 4) };
    match byte_buffer_obj {
        Ok(bb) => **bb,
        Err(e) => {
            error!("Failed to create a direct ByteBuffer: {:?}", e);
            // It is important to free the allocated memory if ByteBuffer creation fails
            unsafe {
                dealloc(rgba_buffer, rgba_layout);
            }
            std::ptr::null_mut()
        }
    }
}
