// detectLine
// original : https://github.com/vl2hmmp/detect-line/tree/master/DetectLine

__kernel void detectLine(__global unsigned char* result, __global unsigned char* origin, int2 size)
{

	int x = get_global_id(0);
	int y = get_global_id(1);

	float val = 0;
	val += 6.828 * origin[(size.x * y + x) * 3];
	val -= origin[(size.x * y + (x - 1)) * 3];
	val -= origin[(size.x * y + (x + 1)) * 3];
	val -= origin[(size.x * (y - 1) + x) * 3];
	val -= origin[(size.x * (y + 1) + x) * 3];
	val -= 0.707 * origin[(size.x * (y - 1) + (x - 1)) * 3];
	val -= 0.707 * origin[(size.x * (y - 1) + (x + 1)) * 3];
	val -= 0.707 * origin[(size.x * (y + 1) + (x - 1)) * 3];
	val -= 0.707 * origin[(size.x * (y + 1) + (x + 1)) * 3];
	result[size.x * y + x] = (unsigned char)val;
}