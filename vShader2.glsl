//  ------  Vertex-Shader mit Beleuchtung  ----------
#version 150
 
uniform mat4 viewMatrix, projMatrix, normalMatrix;    // Transformationsmatrizen
uniform int Lighting;                                 // Beleuchtung ein/aus
uniform vec4 LightDirection;                          // Richtung zur Lichtquelle im Kamera-System
uniform vec4 LightColor;                              // Farbe des Lichtes der Lichtquelle
uniform vec4 Ambient;                                 // Streulicht
   
in vec4 vertexPosition, vertexColor, vertexNormal;    // Vertex-Attributes
 
out vec4 Color;                                       // Vertex-Farbe fuer Fragment-Shader
 
void main()
{  gl_Position = projMatrix * viewMatrix * vertexPosition ;
   Color = vertexColor;
   if ( Lighting == 1 )
   {   vec4 Normal = normalMatrix * vertexNormal;
       float diffuse = max(0.0, dot(normalize(Normal.xyz), normalize(LightDirection.xyz)));
       vec3 scatteredLight = Ambient.rgb + LightColor.rgb * diffuse; 
       vec3 rgb = min(Color.rgb * scatteredLight, vec3(1.0));
       Color = vec4(rgb, Color.a);
   }
   else
     Color = vertexColor;
}
