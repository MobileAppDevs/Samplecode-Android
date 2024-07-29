# Android Barcode/QR Scanner

This Android project provides a simple and efficient way to scan both barcodes and QR codes within your application. It leverages the popular ZXing library to handle the scanning process and returns the scanned content along with an optional image path.

## Features

- **Universal Scanning:** Supports a wide range of barcode and QR code formats.
- **Image Capture (Optional):** Allows you to retrieve an image of the scanned code.
- **Easy Integration:** Seamlessly integrates into your existing Android project.

## Getting Started

1. **Add Dependencies:** Include the following dependencies in your `build.gradle` (Module: app) file:
    ```groovy
    implementation("com.journeyapps:zxing-android-embedded:3.6.0")
    implementation("com.google.zxing:core:3.3.3")
    ```

2. **Initiate Scanning:** Trigger the scanning process when needed (e.g., on a button click).
    ```kotlin
    fun Activity.startScanning() {
        IntentIntegrator(this).apply {
            setOrientationLocked(true)  //prevent screen orientation changes
            setPrompt("Place a barcode or QR code inside the view to scan a code.")  //display prompt message
            setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)  //desired barcode formats to scan
            setBarcodeImageEnabled(true)  //return the image path of the scanned barcode in the result intent
            initiateScan()
        }
    }
    ```

3. **Handle Results:** Process the scanned results in the `onActivityResult` method of your Activity.
    ```kotlin
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK) {
            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            if(result != null) {
                Log.e("abcdefghijklmnopqrstuvwxyz", "handleScanResult: content: ${result.contents} imagePath: ${result.barcodeImagePath}")
                Snackbar.make(this, "Scanned: " + result.contents, Snackbar.LENGTH_LONG).show()
            } else {
                Snackbar.make(this, "Result is empty", Snackbar.LENGTH_SHORT).show()
            }
        } else {
            Snackbar.make(this, "Cancelled", Snackbar.LENGTH_SHORT).show()
        }
    }
    ```

## License

This project is licensed under the MIT License.

## Acknowledgments

- The [ZXing library](https://github.com/zxing/zxing) for providing the barcode scanning functionality.
