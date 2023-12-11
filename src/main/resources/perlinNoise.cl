int constant p[256] = {151, 160, 137, 91, 90, 15, 131, 13, 201,
                95, 96, 53, 194, 233, 7, 225, 140, 36, 103, 30, 69, 142, 8, 99, 37,
                240, 21, 10, 23, 190, 6, 148, 247, 120, 234, 75, 0, 26, 197, 62,
                94, 252, 219, 203, 117, 35, 11, 32, 57, 177, 33, 88, 237, 149, 56,
                87, 174, 20, 125, 136, 171, 168, 68, 175, 74, 165, 71, 134, 139,
                48, 27, 166, 77, 146, 158, 231, 83, 111, 229, 122, 60, 211, 133,
                230, 220, 105, 92, 41, 55, 46, 245, 40, 244, 102, 143, 54, 65, 25,
                63, 161, 1, 216, 80, 73, 209, 76, 132, 187, 208, 89, 18, 169, 200,
                196, 135, 130, 116, 188, 159, 86, 164, 100, 109, 198, 173, 186, 3,
                64, 52, 217, 226, 250, 124, 123, 5, 202, 38, 147, 118, 126, 255,
                82, 85, 212, 207, 206, 59, 227, 47, 16, 58, 17, 182, 189, 28, 42,
                223, 183, 170, 213, 119, 248, 152, 2, 44, 154, 163, 70, 221, 153,
                101, 155, 167, 43, 172, 9, 129, 22, 39, 253, 19, 98, 108, 110, 79,
                113, 224, 232, 178, 185, 112, 104, 218, 246, 97, 228, 251, 34, 242,
                193, 238, 210, 144, 12, 191, 179, 162, 241, 81, 51, 145, 235, 249,
                14, 239, 107, 49, 192, 214, 31, 181, 199, 106, 157, 184, 84, 204,
                176, 115, 121, 50, 45, 127, 4, 150, 254, 138, 236, 205, 93, 222,
                114, 67, 29, 24, 72, 243, 141, 128, 195, 78, 66, 215, 61, 156, 180};

long constant multiplier = 0x5DEECE66DL;
double constant DOUBLE_UNIT = 0x1.0p-53; // 1.0 / (1L << 53)
long constant addend = 0xBL;
long constant mask = (1L << 48) - 1;
long constant seed = {/SEED}; // Replace it before compile!

int next(int bits,
         __global long* internalSeed) {

    long oldseed, nextseed;
    oldseed = seed;
    nextseed = (oldseed * multiplier + addend) & mask;

    // TODO: Save a nextseed to oldseed for after-use
    internalSeed[0] = nextseed;

    return (int)(nextseed >> (48 - bits));
}

int nextInt(int bound,
            __global long* internalSeed) {
    int r = next(31, internalSeed);
    int m = bound - 1;
    if ((bound & m) == 0)  // i.e., bound is a power of 2
        r = (int)((bound * (long)r) >> 31);
    else {
        for (int u = r;
             u - (r = u % bound) + m < 0;
             u = next(31, internalSeed));
    }
    return r;
}

double nextDouble(__global long* internalSeed) {
    return (((long)(next(26, internalSeed)) << 27) + next(27, internalSeed)) * DOUBLE_UNIT;
}

double fade(double x) {
    return x * x * x * (x * (x * 6 - 15) + 10);
}

double lerp(double x, double y, double z) {
    return y + x * (z - y);
}

double grad(int hash, double x, double y, double z) {
    hash &= 15;
    double u = hash < 8 ? x : y;
    double v = hash < 4 ? y : hash == 12 || hash == 14 ? x : z;
    return ((hash & 1) == 0 ? u : -u) + ((hash & 2) == 0 ? v : -v);
}

double genPerlinNoise(double x, double y, double z,
                      double offsetX, double offsetY, double offsetZ,
                      int* perm) {
    x += offsetX;
    y += offsetY;
    z += offsetZ;

    int floorX = floor(x);
    int floorY = floor(y);
    int floorZ = floor(z);

    // Find unit cube containing the point
    int X = floorX & 255;
    int Y = floorY & 255;
    int Z = floorZ & 255;

    // Get relative xyz coordinates of the point within the cube
    x -= floorX;
    y -= floorY;
    z -= floorZ;

    // Compute fade curves for xyz
    double fX = fade(x);
    double fY = fade(y);
    double fZ = fade(z);

    // Hash coordinates of the cube corners
    int A = perm[X] + Y;
    int AA = perm[A] + Z;
    int AB = perm[A + 1] + Z;
    int B = perm[X + 1] + Y;
    int BA = perm[B] + Z;
    int BB = perm[B + 1] + Z;

    return lerp(fZ, lerp(fY, lerp(fX, grad(perm[AA], x, y, z),
                            grad(perm[BA], x - 1, y, z)),
                    lerp(fX, grad(perm[AB], x, y - 1, z),
                            grad(perm[BB], x - 1, y - 1, z))),
                lerp(fY, lerp(fX, grad(perm[AA + 1], x, y, z - 1),
                                grad(perm[BA + 1], x - 1, y, z - 1)),
                        lerp(fX, grad(perm[AB + 1], x, y - 1, z - 1),
                                grad(perm[BB + 1], x - 1, y - 1, z - 1))));
}

double noise(double x, double y, double z, 
             int octaves, double frequency, double amplitude, bool normalized,
             double offsetX, double offsetY, double offsetZ,
             int* perm) {
    double result = 0;
    double amp = 1;
    double freq = 1;
    double max = 0;

    for (int i = 0; i < octaves; i++) {
        result += genPerlinNoise(x * freq, y * freq, z * freq,
                                 offsetX, offsetY, offsetZ,
                                 perm) * amp;
        max += amp;
        freq *= frequency;
        amp *= amplitude;
    }

    if (normalized) {
        result /= max;
    }

    return result;
}

__kernel void perlinNoise(__global double* x,
                          __global double* y,
                          __global double* z,
                          __global int* octaves, 
                          __global double* frequency, 
                          __global double* amplitude,
                          __global int* normalize, // bool
                          __global double* output,
                          __global bool* initialized, // bool
                          __global int* perm,
                          __global double* offsetX,
                          __global double* offsetY,
                          __global double* offsetZ,
                          __global long* internalSeed) {
    int gid = get_global_id(0);

    if (!(initialized[0] == 0)) {
        for (int i = 0; i < 512; i++) {
            perm[i] = p[i & 255];
        }

        offsetX[0] = nextDouble(internalSeed) * 256;
        offsetY[0] = nextDouble(internalSeed) * 256;
        offsetZ[0] = nextDouble(internalSeed) * 256;

        for (int i = 0; i < 256; i++) {
            perm[i] = nextInt(256, internalSeed);
        }

        for (int i = 0; i < 256; i++) {
            int pos = nextInt(256 - i, internalSeed) + i;
            int old = perm[i];

            perm[i] = perm[pos];
            perm[pos] = old;
            perm[i + 256] = perm[i];
        }

        initialized[0] = 1;
    }

    int pers = *perm;
    bool norm = false;
    if (normalize[0] == 1) {
        norm = true;
    }

    output[gid] = noise(x[gid], y[gid], z[gid],
                        octaves[0], frequency[0], amplitude[0], norm,
                        offsetX[0], offsetY[0], offsetZ[0],
                        &pers);
}