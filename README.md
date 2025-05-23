# mutualTLS-Nginx
## Diferencias entre TLS (Transport Layer Security) y mTLS (mutual Transport Layer Security)
Definamos cada protocolo por separado y después vamos con las principales diferencias:
### ¿Qué es un TLS?
Es el protocolo de seguridad estándar que se usa para poder establecer un canal de comunicación seguro a través de una red y es la base de la seguridad web que vemos con los candados en nuestros navegadores.
Este protocolo funciona por medio de un **"handshake"** un tipo de autenticación unidireccional y el proceso tiene una serie de pasos:
* El cliente inicia la conexión enviando un mensaje al servidor, que incluye las versiones de TLS compatibles y en general todas las caracteristicas del soporte.
* El servidor responde con otro mensaje, seleccionando la versión de TLS y el cifrado que se va a usar, y enviando así el certificado digital.
* El cliente verifica la información y validez del certificado enviado por el servidor.
* Si el certificado y toda su información es válida, el cliente y el servidor negocian una clave simétrica para el resto de la comunicación.
* Una vez establecida esa clave, todas las comunicaciones de después entre cliente y servidor se cifran usando esta misma clave.
  
Este protocolo se usa en la navegación segura de paginas webs (HTTPS), para la protección de APIs y el cifrado de comunicaciones entre servidores de correo, entre otras.
### ¿Qué es mTLS?
El **mutual Transport Layer Security** es una extensión del protocolo anteriormente mencionado TLS donde el servidor y cliente se autentican mutuamente utilizando los mismos certificados digitales, lo único que cambia en el proceso es que el cliente no va a ser el único que va a verificar la identidad del servidor, si no que el servidor también va a verificar la identidad del cliente.

### Tabla comparativa entre TLS y mTLS
| Característica        | TLS (Transport Layer Security)                            | mTLS (mutual Transport Layer Security)                       |
| :-------------------- | :-------------------------------------------------------- | :----------------------------------------------------------- |
| **Autenticación** | Unidireccional (cliente autentica servidor)               | Bidireccional (cliente y servidor se autentican)             |
| **Certificados** | Solo el servidor presenta un certificado                  | Ambos (cliente y servidor) presentan certificados            |
| **Complejidad** | Menor                                                     | Mayor (requiere gestión de certificados para ambos)          |
| **Control de Acceso** | Basado en la confianza en el servidor                     | Basado en la confianza mutua y la identidad verificada       |
| **Ideal para** | Comunicaciones públicas (navegación web, APIs públicas)   | Comunicaciones internas seguras, Zero Trust, APIs B2B        |
| **Seguridad** | Alta                                                      | Muy Alta                                                     |
## ¿Qué sucede durante el handshake TLS cuando se usa mTLS?
Este es el paso a paso que sucede cuando se usa específicamente mTLS:
* El cliente envia el mensaje al servidor y contiene la misma información de siempre.
* El servidor le responde con la versión de TLS y el conjunto de cifrado que se va a usar para la conexión, envía su certificado digital y a parte se suma a esta respuesta la solicitud al cliente de que se necesita un certificado del cliente para poder autenticarse.
* El cliente al recibir esta respuesta lo que hace es verificar si el certificado del servidor es valido y si si, el proximo paso es enviar su propio certificado para que el servidor lo pueda validar, adicional a esto el cliente genera una firma diital de parte de los datos del handshake  usando su clave privada.
* Lo que falta es que el servidor valide la información del certificado del cliente, ver si el certificado del cliente no ha caducado o algo parecido, realiza una verificación con la clave pública del cliente para descifrar la firma del mensaje y si coincide con los datos del handshake el servidor confía en el cliente.
* Después de validar todo esto el servidor y el cliente aprueban la autenticación de ambos y se envían un mensaje "Finished" el cual es un hash de toda la comunicación del handshake y se establece la conexión TLS. 
## ¿Qué tipo de información tiene un Keystore y un Trustore?
### Keystore
Repositorio seguro que contiene la **Identidad** (muy importante) digital de una entidad como puede ser un servidor, cliente o aplicación, este repositorio contiene la siguiente información detallada:
* **Clave Privada**: Se usa como firma digital para nuestros datos como para nuestro certificado o descifrar información que previamente ha sido cifrada con nuestra clave pública.
* **Certificado Digital**: En este certificado se encuentra la clave pública anteriormente mencionada y la información de nuestra identidad.

### Trustore
Es un repositorio igual que el keystore pero de **Certificados de entidades en las que ya se ha navegado** la información que contiene este repositorio es la siguiente:
* **Certificados de Autoridades de Certificación**: Estos son los certificados que emiten y firman otros certificados, estas autoridades son las encargadas de decirnos si confiar en una pagina o no.

### ¿Qué riesgos se mitigan al usar mTLS en lugar de autenticación basada en tokens?
## Suplantación de identidad y Ataques de hombre en el medio
Mientras que la autenticación basada en tokens verifica la identidad del cliente a nivel de aplicación después de establecer la conexión TLS, mTLS autentica criptográficamente tanto al cliente como al servidor al inicio del handskahe en la capa de transporte, Esto es un obstaculo gigante para lograr el objetivo del atacante de suplantar alguna de las partes o realizar un ataque de Hombre en el Medio, ya que necesitaría poseer las claves privadas de ambas partes par poder establecer una conexión mTLS válida y no solo interceptar un token.

## Reutilización o robo de tokens
Con la autenticación basada en tokens, un token robado puede ser reutilizado por un atacante hasta su expiración o revocación, presentando un riesgo significativo. mTLS mitiga este riesgo al vincular criptográficamente el token a la conexión TLS establecida con el certificado del cliente. Si un token es robado, el atacante no va a poder establecer la conexión mTLS que se necesita porque no tiene el certificado ni la clave privada del cliente de verdad, lo que no deja que se reutilice el token en un nuevo contexto de conexión.

## Vulnerabilidades en la implementación de tokens
La seguridad de los tokens (especialmente JWTs) depende en gran medida de una implementación correcta, siendo susceptibles a vulnerabilidades como el uso de algoritmos débiles o la alteración del token si no se valida correctamente la firma. mTLS, en cambio, basa su seguridad en la criptografía de clave pública y una Infraestructura de Clave Pública (PKI) bien establecida, donde la autenticación se realiza a un nivel más fundamental de la conexión. Esto elimina los riesgos inherentes a las implementaciones de tokens a nivel de aplicación, ya que la identidad se verifica antes de que cualquier token sea procesado, sin depender de "secretos" compartidos que puedan ser comprometidos o de lógicas de validación complejas que puedan ser explotadas.
