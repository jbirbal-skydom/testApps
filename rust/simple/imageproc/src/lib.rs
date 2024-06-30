// Remove the unused import
use std::slice;
#[cfg(target_os = "android")]
use android_logger::{Config, FilterBuilder};
#[cfg(target_os = "android")]
use log::{info, error, LevelFilter};
use std::alloc::{alloc, Layout, dealloc};


#[cfg(target_os = "android")]
extern crate jni;
#[cfg(target_os = "android")]
use jni::JNIEnv;
#[cfg(target_os = "android")]
use jni::objects::{JClass, JByteBuffer };
#[cfg(target_os = "android")]
use jni::sys::{jobject, jint};


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
pub fn convert_yuv_to_argb(y_data: *const u8, u_data: *const u8, v_data: *const u8, argb_buffer: *mut u8, width: i32, height: i32) {
    let num_pixels: usize = (width * height) as usize;
    let y_plane = unsafe { std::slice::from_raw_parts(y_data, num_pixels) };
    let u_plane = unsafe { std::slice::from_raw_parts(u_data, num_pixels / 4) }; // Assuming subsampling
    let v_plane = unsafe { std::slice::from_raw_parts(v_data, num_pixels / 4) }; // Assuming subsampling

    for y in 0..height {
        for x in 0..width {
            let y_index = (y * width + x) as usize;
            let uv_index = (y / 2) * (width / 2) + (x / 2);

            let y_val = y_plane[y_index] as f32;
            let u_val = u_plane[uv_index as usize] as f32 - 128.0;
            let v_val = v_plane[uv_index as usize] as f32 - 128.0;

            let r = (y_val + 1.402 * v_val).max(0.0).min(255.0) as u8;
            let g = (y_val - 0.344136 * u_val - 0.714136 * v_val).max(0.0).min(255.0) as u8;
            let b = (y_val + 1.772 * u_val).max(0.0).min(255.0) as u8;
            let a = 255; // Alpha channel is fully opaque

            let argb_index = y_index * 4;
            unsafe { *argb_buffer.add(argb_index) = a };
            unsafe { *argb_buffer.add(argb_index + 1) = r };
            unsafe { *argb_buffer.add(argb_index + 2) = g };
            unsafe { *argb_buffer.add(argb_index + 3) = b };
        }
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
    mut env: JNIEnv, 
    class: JClass, 
    y_buffer: JByteBuffer, 
    u_buffer: JByteBuffer, 
    v_buffer: JByteBuffer, 
    width: i32, 
    height: i32
) ->jobject {  // Assuming you might want to return a new Java byte array
        // let input_bytes = env.get_direct_buffer_address(&input).unwrap();
    // process_image(input_bytes, width, height);
    init_logger();
    info!("Processing image with width: {} and height: {}", width, height);

    fn log_pixel_values(rgb_buffer: *const u8, width: i32, height: i32) {
        let positions = [(0, 0), (10, 10), (width-1, height-1)]; // Example positions: top-left, middle, bottom-right
        for (x, y) in positions.iter() {
            let index = (y * width + x) * 4; // Calculate the byte index for pixel (x, y)
            unsafe {
                let r = *rgb_buffer.add(index as usize + 1);
                let g = *rgb_buffer.add(index as usize + 2);
                let b = *rgb_buffer.add(index as usize + 3);
                let a = *rgb_buffer.add(index as usize);
                info!("Pixel at ({}, {}): ARGB({}, {}, {}, {})", x, y, a, r, g, b);
            }
        }
    }

    // info!("Starting to process buffer of size: {}", size);
    // info!("Buffer address: {:p}, size: {}", input_bytes, size);

    // Assuming Y, U, V data are each in contiguous blocks of memory
    let num_pixels = (width * height) as usize; // Number of pixels in the Y plane
    let y_stride = width as usize; // This might need adjustment based on actual stride info

    // Convert the raw pointers to slices for safer access
    let y_data = env.get_direct_buffer_address(&y_buffer).expect("Failed to get buffer address for Y");
    let u_data = env.get_direct_buffer_address(&u_buffer).expect("Failed to get buffer address for U");
    let v_data = env.get_direct_buffer_address(&v_buffer).expect("Failed to get buffer address for V");
    // Simple brightness mask: increase brightness by 30 (example value)

    // Assuming Y, U, V data are each in contiguous blocks of memory
    let num_pixels = (width * height) as usize; // Number of pixels in the Y plane

    // Convert the raw pointer to a mutable slice for easier manipulation
    let y_plane = unsafe { std::slice::from_raw_parts_mut(y_data, num_pixels) };


    let brightness_increase = 30;
    for y in y_plane.iter_mut() {
        *y = y.saturating_add(brightness_increase);
    }

    // Allocate new buffer for RGB output
    let num_pixels = (width * height) as usize;
    let argb_layout = Layout::array::<u8>(num_pixels * 4).unwrap();
    let argb_buffer = unsafe { alloc(argb_layout) };
    info!("Created image  buffer size: {} and location: {:?}", num_pixels, argb_buffer);
    log_pixel_values(argb_buffer, width, height);


    

    if argb_buffer.is_null() {
        // Handle allocation failure
        error!("Failed to allocate memory for RGB buffer");
    }

    // Assume a function convert_yuv_to_rgb exists
    convert_yuv_to_argb(y_data, u_data, v_data, argb_buffer, width, height);


    // Create a ByteBuffer from the Rust-allocated RGB buffer to return to Java
    // let byte_buffer = unsafe { env.new_direct_byte_buffer(rgb_buffer, (num_pixels * 3) as usize).unwrap() };
    log_pixel_values(argb_buffer, width, height);
    


    info!("Finished processing buffer");
        // Create a ByteBuffer from the Rust-allocated RGB buffer to return to Java
        let byte_buffer_obj = unsafe { env.new_direct_byte_buffer(argb_buffer, num_pixels * 4) };
        match byte_buffer_obj {
            Ok(bb) => **bb,
            Err(e) => {
                error!("Failed to create a direct ByteBuffer: {:?}", e);
                // It is important to free the allocated memory if ByteBuffer creation fails
                unsafe {
                    dealloc(argb_buffer, argb_layout);
                }
                std::ptr::null_mut()
            }
        }
    }
    
