#pragma once
#include<iostream>
using namespace std;
class WindowRange
{
public:
	WindowRange();
	WindowRange(float a, float b, float c, string s) :start(a), end(b), mz(c), features(s) {}
	WindowRange(const WindowRange&);

	void setSatrt (float);
	void setEnd (float);
	void setMz (float);
	void setFeatures (string);

	float getStart();
	float getEnd();
	float getMz();
	string getFeatures();
	
	template<class Object>
	bool equals(Object obj);
private:
	/**
	 * precursor mz start
	 */
	float start;
	/**
	 * precursor mz end
	 */
	float end;
	/**
	 * precursor mz
	 */
	float mz;

	/**
	 * À©Õ¹×Ö¶Î
	 */
	string features;
};

template<typename T>
class CInstanceof {
public:
	static bool testsuper(const T&) { return true; }
	template<typename U>
	static bool testsuper(U&) { return false; }
};
template<typename T1, typename T2>
bool instanceof(const T2&) {
	return CInstanceof<T1>::testsuper(T2());
}