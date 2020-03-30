#pragma once
#include <iostream>
using namespace std;
class Features
{
public:
	static string original_width;
	static string original_precursor_mz_start;
	static string original_precursor_mz_end;
	static string raw_id;
	static string ignore_zero_intensity;
	static string overlap;
	static string slope;
	static string intercept;

};

/**
	 * 原始文件中前体的窗口大小,未经过overlap参数调整,precursor mz
	 */
string Features::original_width = "owid";

/**
 * 原始文件中前体的荷质比窗口开始位置,未经过overlap参数调整,precursor mz
 */
string Features::original_precursor_mz_start = "oMzStart";

/**
 * 原始文件中前体的荷质比窗口结束位置,未经过overlap参数调整,precursor mz
 */
string Features::original_precursor_mz_end = "oMzEnd";

/**
 * 从Vendor文件中得到的msd.id
 */
string Features::raw_id = "rawId";

/**
 * 是否忽略Intensity为0的键值对,默认为true
 */
string Features::ignore_zero_intensity = "ignoreZeroIntensity";

/**
 * SWATH的各个窗口间的重叠部分
 */
string Features::overlap = "overlap";

/**
 * irt所得斜率
 */
string Features::slope = "slope";

/**
 * irt所得截距
 */
string Features::intercept = "intercept";

