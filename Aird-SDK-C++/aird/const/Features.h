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
	 * ԭʼ�ļ���ǰ��Ĵ��ڴ�С,δ����overlap��������,precursor mz
	 */
string Features::original_width = "owid";

/**
 * ԭʼ�ļ���ǰ��ĺ��ʱȴ��ڿ�ʼλ��,δ����overlap��������,precursor mz
 */
string Features::original_precursor_mz_start = "oMzStart";

/**
 * ԭʼ�ļ���ǰ��ĺ��ʱȴ��ڽ���λ��,δ����overlap��������,precursor mz
 */
string Features::original_precursor_mz_end = "oMzEnd";

/**
 * ��Vendor�ļ��еõ���msd.id
 */
string Features::raw_id = "rawId";

/**
 * �Ƿ����IntensityΪ0�ļ�ֵ��,Ĭ��Ϊtrue
 */
string Features::ignore_zero_intensity = "ignoreZeroIntensity";

/**
 * SWATH�ĸ������ڼ���ص�����
 */
string Features::overlap = "overlap";

/**
 * irt����б��
 */
string Features::slope = "slope";

/**
 * irt���ýؾ�
 */
string Features::intercept = "intercept";

