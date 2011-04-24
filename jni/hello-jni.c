/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
#include <string.h>
#include <jni.h>
#include <stdio.h>
#include <android/log.h>
/*
 * File pathes of hardware state on Nexus S
 */
#define SCREEN_NEXUS_S_PATH "/sys/devices/platform/s3cfb/spi_gpio.3/spi3.0/backlight/s5p_bl/brightness"
#define CPU_CUR_FREQ_PATH "/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq"

/*
 * File pathes of hardware state on Emulator
 */
#define SCRREN_EMULATOR_PATH "NONE"

/*
 * File pathes that are the same in both emualtor and Nexus S
 */
#define CPU_UTIL_PATH "/proc/stat"
#define BATTERY_CAP_PATH "/sys/class/power_supply/battery/capacity"
#define BATTERY_VOLTAGE_PATH "/sys/class/power_supply/battery/voltage_now"
#define WIFI_PATH "/proc/self/net/dev"
#define SD_CARD_PATH "/proc/diskstats"



/* This is a trivial JNI example where we use a native method
 * to return a new VM String. See the corresponding Java source
 * file located at:
 *
 *   apps/samples/hello-jni/project/src/com/example/HelloJni/HelloJni.java
 */
jstring
Java_com_example_hellojni_SamplingService_readScreenLevel( JNIEnv* env,
															  jobject thiz )
{
	FILE * fp;
	
	fp=fopen(SCREEN_NEXUS_S_PATH,"r");
	if(fp==NULL) return;
	
	char p[80];
	fgets(p,80,fp);
	p[strlen(p)-1]=0;
	fclose(fp);
	
	
	
    return (*env)->NewStringUTF(env, p);
}

jstring
Java_com_example_hellojni_SamplingService_readCpuFreq( JNIEnv* env,jobject thiz )
{
	FILE * fp;
	fp = fopen(CPU_CUR_FREQ_PATH, "r");
	if(fp==NULL) return;
	
	char s[256];
	fgets(s,256,fp);
	
	s[strlen(s)-1]=0;
	fclose(fp);
	
    return (*env)->NewStringUTF(env, s);
}


jstring
Java_com_example_hellojni_SamplingService_readBatteryCapacity( JNIEnv* env,
															  jobject thiz )
{
	
	FILE * fp;
	fp=fopen(BATTERY_CAP_PATH,"r");
	if(fp==NULL) return;
	
	char s[80];
	fgets(s,80,fp);
	fclose(fp);
	
	
	
	
    return (*env)->NewStringUTF(env, s);
}

jstring
Java_com_example_hellojni_SamplingService_readBatteryVolatage( JNIEnv* env,
															  jobject thiz )
{
	
	FILE * fp;
	fp=fopen(BATTERY_VOLTAGE_PATH,"r");
	if(fp==NULL) return;
	
	char s[80];
	fgets(s,80,fp);
	fclose(fp);
	
    // __android_log_print(ANDROID_LOG_WARN, "android123-cwj", "from ndk = %s", s);
	
	
    return (*env)->NewStringUTF(env, s);
}

jstring
Java_com_example_hellojni_SamplingService_readDiskStatus( JNIEnv* env,
														 jobject thiz )
{
    int n;
    int f1;
	// char buf[200];
    char s[2000];
    f1 = open(SD_CARD_PATH,0);
    n=read(f1,s,2000);
	s[n]=0;
    close(f1);
	
	/*	FILE * fp;
	 fp=fopen(SD_CARD_PATH,"r");
	 if(fp==NULL) return;
	 
	 char s[500];
	 
	 
	 fgets(s,500,fp);
	 fclose(fp);
	 */
    // __android_log_print(ANDROID_LOG_WARN, "android123-cwj", "from ndk = %s", s);
	
	
    return (*env)->NewStringUTF(env, s);
}

jstring
Java_com_example_hellojni_SamplingService_readCpuInfo( JNIEnv* env,
													  jobject thiz )
{
	/*
	 int n;
	 int f1;
	 // char buf[200];
	 char s[2000];
	 f1 = open(CPU_UTIL_PATH,0);
	 while((n=read(f1,s,2000))>0)
	 {
	 //    strcat(s,buf);
	 
	 }
	 */
	
	FILE * fp;
	fp=fopen(CPU_UTIL_PATH,"r");
	if(fp==NULL) return;
	
	char s[500];
	fgets(s,500,fp);
	fclose(fp);
	
    // __android_log_print(ANDROID_LOG_WARN, "android123-cwj", "from ndk = %s", s);
	
	
    return (*env)->NewStringUTF(env, s);
}

jstring
Java_com_example_hellojni_SamplingService_readWifiStatus( JNIEnv* env,
														 jobject thiz )
{
    int n;
    int f1;
	// char buf[200];
    char s[1500];
    f1 = open(WIFI_PATH,0);
	n = read(f1,s,sizeof(s)-1);
    s[n] = 0;	
	/*
	 char buffer[256];
	 const int len = read(fd, buffer, sizeof(buffer)-1);
	 close(fd);
	 
	 if (len < 0) {
	 //LOGW("Unable to open process file: %s fd=%d\n", file8, fd);
	 return JNI_FALSE;
	 }
	 buffer[len] = 0;
	 
	 
	 */
    close(f1);
	
	/*	FILE * fp;
	 fp=fopen(SD_CARD_PATH,"r");
	 if(fp==NULL) return;
	 
	 char s[500];
	 
	 
	 fgets(s,500,fp);
	 fclose(fp);
	 */
    // __android_log_print(ANDROID_LOG_WARN, "android123-cwj", "from ndk = %s", s);
	
	
    return (*env)->NewStringUTF(env, s);
}
