package com.example.hellojni;

public class PowerModelParam {
	/**
	 * No power consumption, or accounted for elsewhere.
	 */
	public static final String POWER_NONE = "none";

	/**
	 * Power consumption when CPU is in power collapse mode.
	 */
	public static final String POWER_CPU_IDLE = "cpu.idle";

	/**
	 * Power consumption when CPU is running at normal speed.
	 */
	public static final String POWER_CPU_NORMAL = "cpu.normal";

	/**
	 * Power consumption when CPU is running at full speed.
	 */
	public static final String POWER_CPU_FULL = "cpu.full";

	/**
	 * Power consumption when WiFi driver is scanning for networks.
	 */
	public static final String POWER_WIFI_SCAN = "wifi.scan";

	/**
	 * Power consumption when WiFi driver is on.
	 */
	public static final String POWER_WIFI_ON = "wifi.on";

	/**
	 * Power consumption when WiFi driver is transmitting/receiving.
	 */
	public static final String POWER_WIFI_ACTIVE = "wifi.active";

	/**
	 * Power consumption when GPS is on.
	 */
	public static final String POWER_GPS_ON = "gps.on";

	/**
	 * Power consumption when Bluetooth driver is on.
	 */
	public static final String POWER_BLUETOOTH_ON = "bluetooth.on";

	/**
	 * Power consumption when Bluetooth driver is transmitting/receiving.
	 */
	public static final String POWER_BLUETOOTH_ACTIVE = "bluetooth.active";

	/**
	 * Power consumption when Bluetooth driver gets an AT command.
	 */
	public static final String POWER_BLUETOOTH_AT_CMD = "bluetooth.at";

	/**
	 * Power consumption when screen is on, not including the backlight power.
	 */
	public static final String POWER_SCREEN_ON = "screen.on";

	/**
	 * Power consumption when cell radio is on but not on a call.
	 */
	public static final String POWER_RADIO_ON = "radio.on";

	/**
	 * Power consumption when talking on the phone.
	 */
	public static final String POWER_RADIO_ACTIVE = "radio.active";

	/**
	 * Power consumption at full backlight brightness. If the backlight is at
	 * 50% brightness, then this should be multiplied by 0.5
	 */
	public static final String POWER_SCREEN_FULL = "screen.full";

	/**
	 * Power consumed by the audio hardware when playing back audio content. This is in addition
	 * to the CPU power, probably due to a DSP and / or amplifier.
	 */
	public static final String POWER_AUDIO = "dsp.audio";

	/**
	 * Power consumed by any media hardware when playing back video content. This is in addition
	 * to the CPU power, probably due to a DSP.
	 */
	public static final String POWER_VIDEO = "dsp.video";


}
