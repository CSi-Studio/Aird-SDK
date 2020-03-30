#pragma once
#include<iostream>
using namespace std;
class SymbolConst {
public:
	static const string COMMA;
	static const string TAB;
	static const string DOT;
};
const string SymbolConst::COMMA = ",";
const string SymbolConst::TAB = "\t";
const string SymbolConst::DOT = ".";

