float exampleFunction(boolean var1, int var2)
	if(var1) {
		return (float)var2;
	} else {
		return 94e-1;
	}
}

int main() {
	boolean b = true;
	boolean c = !b;
	int d = 4420;
	float r = exampleFunction(c, d);
	float a = 1;
	string s = "ab
    c";
	s = "abc\basdfa";
	s = "abc\fasdfa";
	s = "abc\nasdfa";
	s = "abc\tasdfa";
	s = "abc\'asdfa";
	s = "abc\"asdfa";
	s = "abc\\asdfa";
	s = "abc\aasdfa";
	return 0;
}
