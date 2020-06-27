#version 330

varying highp vec4 v_color;
varying highp vec2 v_texCoords;
uniform sampler2D u_texture;

void main() {
    vec4 color = v_color * texture2D(u_texture, v_texCoords);
    gl_FragColor = vec4(1, 1, 1, color.a);
}