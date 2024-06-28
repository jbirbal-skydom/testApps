// use std::ptr;

// Assuming the function signature from your library
use imgproc::process_image;

fn main() {
    let width = 2;
    let height = 2;
    let mut image_data = [255, 0, 0,   // Red pixel
                          0, 255, 0,   // Green pixel
                          0, 0, 255,   // Blue pixel
                          255, 255, 0]; // Yellow pixel

    unsafe {
        process_image(image_data.as_mut_ptr(), width, height);
    }

    println!("Processed Image Data:");
    for i in image_data.iter() {
        print!("{} ", i);
    }
}