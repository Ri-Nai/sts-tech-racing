#version 150

#define PI   3.1415926535
#define TWO_PI 6.2831853070

uniform sampler2D u_texture;
uniform mat4 u_projTrans;

varying vec4 v_color;
varying vec2 v_texCoord0;

uniform float radius = 0.45;
uniform float diameter = 0.9;

//uniform float theta0 = PI / 2.0;
//uniform float theta1 = PI;
//uniform float theta2 = (3 * PI) / 2.0;

//uniform mat4 colors = mat4(
    //vec4(0.380, 0.102, 0.133, 1.0), // attacks
    //vec4(0.165, 0.282, 0.165, 1.0), // skills
    //vec4(0.125, 0.290, 0.482, 1.0), // powers
    //vec4(0.224, 0.165, 0.259, 1.0)  // curses
//);

uniform float borderSize;

uniform int numThetas;
uniform float thetas[32];

uniform int numColors;
uniform vec4 colors[32];

// --------------------------------------------------------------------------------
// Simple helpers
// --------------------------------------------------------------------------------

// Returns the angle in [0, 2PI] given a point.
float computeTheta(vec2 pos) {
    return TWO_PI - (atan(pos.y, pos.x) + PI);
}

// Combines the given color with an amt (0 -> 1.0) of white to brighten it.
vec4 blendWithWhite(vec4 color, float amt) {
    return vec4(clamp(color.rgb + amt, 0.0, 1.0), color.a);
}

// Computes the alpha component of a color. This makes sure to smoothly
// become transparent as the dist gets towards the desired radius (and 
// fully transparent beyond) to make a visually antialiased effect.
float computeAlpha(float dist) {
    return 1.0 - smoothstep(radius - borderSize, 
                            radius + borderSize, 
                            dist);
}

// --------------------------------------------------------------------------------
// Math helpers
// --------------------------------------------------------------------------------

/*
 
// Produces a one-hot vector describing which section of the chart
// we're in. e.g. (1, 0, 0, 0) says we're in the space from 
// [theta = 0 → theta = theta_0)
vec4 computeOneHotLocation(float theta) {
    float in0_1 = step(0,      theta) * step(theta, theta0);
    float in1_2 = step(theta0, theta) * step(theta, theta1);
    float in2_3 = step(theta1, theta) * step(theta, theta2);
    float in3_4 = step(theta2, theta) * step(theta, TWO_PI);

    return vec4(in0_1, in1_2, in2_3, in3_4);
}
*/

// Computes the distance between a point and a normalized vector defining a line.
// Assumes the origin is (0.5, 0.5). The algorithm here is simply projecting the 
// vector from the origin to the point onto the given normal line, and then 
// looking at the vertical component of the triangle formed.
float polarLinePointDistance(vec2 point, vec2 lineNormalized) {
    vec2 pma = point - vec2(0.5);
    return length(pma - (dot(pma, lineNormalized)) * lineNormalized);
}

// Returns a normal vector along a line defined by the given polar angle
vec2 getNormalLineThroughAngle(float angle) {
    return normalize(vec2(cos(angle), sin(angle)));
}

// Given a line ACB defined from targetTheta through origin C and 
// a point (P) returns true if P is closer to A than B (e.g. on the 
// "correct" half of the line close to segment AC instead of segment 
// CB).
bool onProperSide(vec2 point, float targetTheta) {
    vec2 a = vec2(0.5) + vec2(cos(targetTheta), sin(targetTheta));
    vec2 b = vec2(0.5) - vec2(cos(targetTheta), sin(targetTheta));

    return distance(point, a) < distance(point, b);
}

// --------------------------------------------------------------------------------
// Adjust the chosen color and turn it white if along a border
// --------------------------------------------------------------------------------

// Adds white to the outer edge of the circle.
vec4 applyOuterBorder(float dist, vec4 color) {
    float borderStart = radius - borderSize;
    float borderMid = radius;
    float borderEnd = radius + borderSize;

    if (dist > borderStart && dist < borderEnd) {
        if (dist < borderMid) {
            return blendWithWhite(color, smoothstep(borderStart, borderMid, dist));
        }
        else {
            return blendWithWhite(color, 1.0 - smoothstep(borderMid, borderEnd, dist));
        }
    }
    else {
        return color;
    }
}

vec4 applyInnerBorder(vec2 point, vec4 color) {
    //vec2 lineDefault = getNormalLineThroughAngle(0);
    //vec2 line0 = getNormalLineThroughAngle(theta0);
    //vec2 line1 = getNormalLineThroughAngle(theta1);
    //vec2 line2 = getNormalLineThroughAngle(theta2);

    float lower = 0.0;
    float upper = borderSize / 2.0;

    //float de = onProperSide(point, 0)      ? 1.0 - smoothstep(lower, upper, polarLinePointDistance(point, lineDefault)) : 0.0;
    //float d0 = onProperSide(point, theta0) ? 1.0 - smoothstep(lower, upper, polarLinePointDistance(point, line0)) : 0.0;
    //float d1 = onProperSide(point, theta1) ? 1.0 - smoothstep(lower, upper, polarLinePointDistance(point, line1)) : 0.0;
    //float d2 = onProperSide(point, theta2) ? 1.0 - smoothstep(lower, upper, polarLinePointDistance(point, line2)) : 0.0;

    // Do for the theta = 0 line
    vec2 line = getNormalLineThroughAngle(0);
    float amt = onProperSide(point, 0) ? 1.0 - smoothstep(lower, upper, polarLinePointDistance(point, line)) : 0.0;
    color = blendWithWhite(color, amt);

    // And then the rest of the thetas
    for (int i = 0; i < numThetas; ++i) {
        vec2 line = getNormalLineThroughAngle(thetas[i]);
        float amt = onProperSide(point, thetas[i]) ? 1.0 - smoothstep(lower, upper, polarLinePointDistance(point, line)) : 0.0;

        color = blendWithWhite(color, amt);
    }

    //color = blendWithWhite(color, de);
    //color = blendWithWhite(color, d0);
    //color = blendWithWhite(color, d1);
    //color = blendWithWhite(color, d2);

    return color;
}

vec4 getBaseColor(float theta) {
    for (int i = 0; i < numThetas; ++i) {
        if (theta < thetas[i])
            return colors[i];
    }

    return colors[numColors - 1];
}

// --------------------------------------------------------------------------------

void main()
{
    // Compute some basic information about this pixel
    vec2 diffFromCenter = vec2(0.5) - v_texCoord0;
    float distFromCenter = distance(vec2(0.5), v_texCoord0);

    float theta = computeTheta(diffFromCenter);

    // Base Colors
    vec4 color = vec4(getBaseColor(theta).rgb, computeAlpha(distFromCenter));

    // Borders render on top of the colors
    color = applyOuterBorder(distFromCenter, color);
    color = applyInnerBorder(vec2(v_texCoord0.x, 1.0 - v_texCoord0.y), color);

    // Output
    gl_FragColor = color;
}
