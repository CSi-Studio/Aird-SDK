#include "pch.h"
#include "WindowRange.h"
WindowRange::WindowRange() {
	start = (float) 0;
	end = (float) 2000;
	mz = (float)1000;
	features = "1223345";
}

WindowRange::WindowRange(const WindowRange& b) {
	start = b.start;
	end = b.end;
	mz = b.mz;
	features = b.features;
}

void WindowRange::setSatrt (float s) {
	start = s;
}

void WindowRange::setEnd (float e) {
	end = e;
}

void WindowRange::setMz(float m) {
	mz = m;
}

void WindowRange::setFeatures(string f) {
	features = f;
}

float WindowRange::getStart() {
	return start;
}

float WindowRange::getEnd() {
	return end;
}

float WindowRange::getMz() {
	return mz;
}

string WindowRange::getFeatures() {
	return features;
}

template<class Object>
bool WindowRange::equals(Object obj) {
	Object *ob ;
	*ob = obj;
	if (ob== NULL) {
		return false;
	}

	/*
	ʹ��dynamic_cast�����Java�е�instanceof
	����WindowRange������,WindowRange& w = dynamic_cast<WindowRange&>(obj);��ת��Ϊ�������������׳�bad_cast�쳣
	�����ȱ�� ��Object���Ƕ�̬��(������WindowRange���������Objectû���麯��(�����Ǽ̳л��Զ���))�����뱨��
	*/
	try {
		WindowRange& w = dynamic_cast<WindowRange&>(obj);
		WindowRange windowRange;
		windowRange = obj;
		if (start == NULL || end == NULL || windowRange.getStart() == NULL || windowRange.getEnd == NULL) {
			return false;
		}
		else {
			return ((start == windowRange.getStart()) && (end == windowRange.getEnd()));//����ֵ��ͬ,��ΪC++�л������Ϳ���ʹ��"=="�Ƚ�����
		}
	} catch (exception e) {
		cout << e.what();
		return false;
	}
	
}