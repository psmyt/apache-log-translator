# org.apache.http.wire log translator

have you seen something like this and tried to make sense of it? 
```
2024-05-22 07:29:45.318 DEBUG 23 [some-service,6897300b46154ff7b0eef4f44ea3ad99]  --- [http-nio-8080-exec-8] org.apache.http.wire : http-outgoing-17 << "[0x1f][0x8b][0x8][0x0][0x0][0x0][0x0][0x0][0x0][0x3][0xaa]V[0xca]HMLI-R[0xb2][0xaa]V*N-*K-[\n]"
2024-05-22 07:29:45.318 DEBUG 23 [some-service,6897300b46154ff7b0eef4f44ea3ad99]  --- [http-nio-8080-exec-8] org.apache.http.wire : http-outgoing-17 << "[0xc9][0xcc]MU[0xb2]R2202[0xd1]50[0xd5]52R00[0xb7]2[0xb2][0xb4]21[0xd5]36[0xb0]P[0xd0]6060P[0xaa][0xd5]Q*J-.[0xcd])[0x1][0xaa][0xf4][0xf7]V[0xaa][0x5][0x0][0x0][0x0][0xff][0xff][0x3][0x0][0xe3][0x4][0x0]xG[0x0][0x0][0x0][\r][\n]"
```
opened a hex editor and manually typed all the byte literals mixed with ASCII to read the damn message?

well you don't have to tear your hair out and go punch a hole in the wall because I've already done those things and this right here piece of code can re-assemble the binary, decompress it (if needed), and output it into your console

## FEATURES:
automatically detects gzip
