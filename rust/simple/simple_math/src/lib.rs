// This ensures we bring in JNI bindings only when compiling for Android.
#[cfg(target_os = "android")]
extern crate jni;

// Function definition for Android
// This includes JNI-specific parameters for interfacing with Java.
#[cfg(target_os = "android")]
#[no_mangle]
pub extern "C" fn Java_ai_skydom_calculator_NativeLib_addNumbers(
    env: jni::JNIEnv, 
    class: jni::objects::JClass, 
    a: i32, 
    b: i32
) -> i32 {
    a + b + 1
}



// Function definition for iOS
// A simpler version without JNI, suitable for calling from Swift/Objective-C.
#[cfg(target_os = "ios")]
#[no_mangle]
pub extern "C" fn add_numbers(a: i32, b: i32) -> i32 {
    a + b +1 
}


pub fn add(left: usize, right: usize) -> usize {
    left + right
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn it_works() {
        let result = add(2, 2);
        assert_eq!(result, 4);
    }
}
