
#ifndef _HELLO_CL_
#define _HELLO_CL_

const char* HELLO_CL =
    "__kernel void hello(__global char* string)\n"
    "{\n"
    "  string[0] = 'H';\n"
    "  string[1] = 'e';\n"
    "  string[2] = 'l';\n"
    "  string[3] = 'l';\n"
    "  string[4] = 'o';\n"
    "  string[5] = ',';\n"
    "  string[6] = ' ';\n"
    "  string[7] = 'W';\n"
    "  string[8] = 'o';\n"
    "  string[9] = 'r';\n"
    "  string[10] = 'l';\n"
    "  string[11] = 'd';\n"
    "  string[12] = '!';\n"
    "  string[13] = '\\0';\n"
    "}";

#endif