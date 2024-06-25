#[no_mangle]
pub extern "C" fn Java_ai_skydom_calculator_NativeLib_addNumbers(
    env: jni::JNIEnv, 
    class: jni::objects::JClass, 
    a: i32, 
    b: i32
) -> i32 {
    a + b + 1
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
