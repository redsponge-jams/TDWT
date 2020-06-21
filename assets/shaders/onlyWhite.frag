#version 330

varying highp vec4 v_color;
varying highp vec2 v_texCoords;
uniform sampler2D u_texture;

void main() {
    vec4 color = v_color * texture2D(u_texture, v_texCoords);
    color.a = (color.r + color.g + color.b + color.a == 0) ? 0 : 1f;
//
    color.rgb = vec3(1,1,1);
    gl_FragColor = color;
}