use image;
use imgproc; // Import your library

use image::{ImageBuffer, RgbaImage};
use imgproc::process_image;

fn main() {
    let img_path = "D:\\Coding\\git\\testApps\\images\\logo.png"; // Specify the path to your image
    let mut img = image::open(img_path).unwrap().to_rgba8(); // Open and convert the image to RGB8

    let (width, height) = img.dimensions();
    let raw_img = img.as_mut();
    let buffer = raw_img.as_mut_ptr();

    unsafe {
        process_image(buffer, width as i32, height as i32);
    }

    img.save("D:\\Coding\\git\\testApps\\images\\processed_image.png").unwrap(); // Save the processed image
    println!("Image processed and saved successfully.");
}