# StepView
## How to
To get a Git project into your build:

### Such as "gradle": (link：https://jitpack.io/#YIHwork/StepView/1.0)
Step 1. Add the JitPack repository to your build file

gradle
maven
sbt
leiningen
Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
Step 2. Add the dependency

	dependencies {
	        implementation 'com.github.YIHwork:StepView:1.0'
	}
