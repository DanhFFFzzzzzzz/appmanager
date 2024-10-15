buildscript {
    dependencies {
        classpath("com.google.gms:google-services:4.4.2")
    }
}
// Tệp build cấp cao nơi bạn có thể thêm cấu hình chung cho tất cả các dự án/module con.
plugins {
    id("com.android.application") version "8.1.4" apply false
}


