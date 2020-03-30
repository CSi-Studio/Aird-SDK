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
	使用dynamic_cast来替代Java中的instanceof
	若是WindowRange的子类,WindowRange& w = dynamic_cast<WindowRange&>(obj);则转换为引用若不是则抛出bad_cast异常
	其存在缺陷 若Object不是多态的(即不是WindowRange的子类或者Object没有虚函数(无论是继承或自定义))则会编译报错
	*/
	try {
		WindowRange& w = dynamic_cast<WindowRange&>(obj);
		WindowRange windowRange;
		windowRange = obj;
		if (start == NULL || end == NULL || windowRange.getStart() == NULL || windowRange.getEnd == NULL) {
			return false;
		}
		else {
			return ((start == windowRange.getStart()) && (end == windowRange.getEnd()));//两者值相同,因为C++中基础类型可以使用"=="比较内容
		}
	} catch (exception e) {
		cout << e.what();
		return false;
	}
	
}