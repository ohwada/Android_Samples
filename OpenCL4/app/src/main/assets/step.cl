// Copyright © 2014 Intel Corporation. All rights reserved.
//
// WARRANTY DISCLAIMER
//
// THESE MATERIALS ARE PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
// "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
// LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
// A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL INTEL OR ITS
// CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
// EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
// PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
// PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY
// OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY OR TORT (INCLUDING
// NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THESE
// MATERIALS, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
//
// Intel Corporation is the author of the Materials, and requests that all
// problem reports or change requests be submitted to it directly


kernel void stepKernel
(
    global const int* inputPixels,
    global int* outputPixels,
    const uint rowPitch,
    const int stepCount,
    const int xTouch,
    const int yTouch,
    const int radiusHi,
    const int radiusLo
)
{
    float3 channelWeights = (float3)(0.299f, 0.587f, 0.114f);

    int x = get_global_id(0);
    int y = get_global_id(1);
    int imageHeight = get_global_size(0);

    int inPixel = inputPixels[x + y*rowPitch];
    int outPixel = 0xffffffff;   // white border

    int xRel = x - xTouch;
    int yRel = y - yTouch;
    int polar = xRel*xRel + yRel*yRel;
 
    if(polar < radiusLo)
    {
        // Inner area of the circle

        // Disassembly channels
        float3 channels = { (inPixel & 0xff), (inPixel & 0xff00) >> 8, (inPixel & 0xff0000) >> 16 };

        // Calculate gray value based on the canonical channel weights
        uint gray = dot(channels, channelWeights);

        // Assembly final color
        outPixel = 0xff000000 | (gray << 16) | (gray << 8) | gray;
    }
    else if(polar > radiusHi)
    {
        // Outter area of the circle
        outPixel = inPixel;
    }

    outputPixels[x + y*rowPitch] = outPixel;
}
