# Duuba XAdES
Duuba XAdES is a library for creating XML advanced electronic signatures (XAdES) for sealing and signing. 
Use it to create XAdES baseline signatures according to ETSI specifications *[TS 101 903 v1.4.1](https://www.etsi.org/deliver/etsi_ts/101900_101999/101903/01.04.01_60/ts_101903v010401p.pdf)* and *[EN 319 132 v1.1.1](https://www.etsi.org/deliver/etsi_en/319100_319199/31913201/01.01.01_60/en_31913201v010101p.pdf)*.  
  
Duuba supports both ETSI specifications of XAdES. 
The main difference between these two specifications is that for some of the qualifying properties EN 319 132 defines additional data or uses another representation of the same data. 
Duuba therefore includes new versions of the corresponding XML element declarations in the XML schema (the elements with "V2" suffix). 
There are also different classes to represent the different versions of these properties. 
Duuba will automatically create the correct elements based on the specification set when creating the signature. 

__________________

For more information on Duuba visit the project website at https://duuba.org  
Lead developer: Sander Fieten  
Code hosted at https://github.com/duuba/xades  
Issue tracker https://github.com/duuba/xades/issues  
  

## Features
- Creates XAdES baseline signatures
- can be used for signing EESSI documents
- Fast - signing a 1.2GB document takes 9.4 seconds
- Compact 
- Easy to use
- Well documented in the code
- Can handle large documents (tested with documents up to 1.2GB) 
- Low memory usage


## Using
Duuba is built on top of the Apache Santuario library for the processing of XML signatures. 
It follows the standard Java XML factory pattern and adds the classes and factory methods for the elements representing the XAdES signature and its qualifying attributes. 
This means that you will need to use both the factory from Santuario to create the “normal” XML signature object and use `org.duuba.xades.XadesSignatureFactory` to create the XAdES specific ones. 
To ensure you use the correct XMLSignatureFactory instance use the `XadesSignatureFactory.getXMLSignatureFactory()` method.

To facilitate the creation of an enveloped XAdES baseline signature we have included a builder, `org.duuba.xades.builders.BasicEnvelopedSignatureBuilder``, that takes care of creating all necessary elements of the XAdES signature. 
You provide the private key, certificate and values for the qualifying properties to include and the builder will take care of constructing the XAdES signature. 
An example of how the builder can be used to create the signature can be found in `[org.duuba.xades.examples.EnvelopedBBExample](src/test/java/org/duuba/xades/examples/EnvelopedBBExample.java)`.


## Contributing
We are using the simplified Github workflow to accept modifications which means you should:
* create an issue related to the problem you want to fix or the function you want to add (good for traceability and cross-reference)
* fork the repository
* create a branch (optionally with the reference to the issue in the name)
* write your code, including comments 
* commit incrementally with readable and detailed commit messages
* run integration tests to check everything works on runtime
* Update the changelog with a short description of the changes including a reference to the issues fixed
* submit a pull request *against the 'next' branch* of this repository

If your contribution is more than a patch, please contact us beforehand to discuss which branch you can best submit the pull request to.

### Submitting bugs
You can report issues directly on the [project Issue Tracker](https://github.com/duuba/xades/issues).
Please document the steps to reproduce your problem in as much detail as you can (if needed and possible include screenshots).

## Versioning
Version numbering follows the [Semantic versioning](http://semver.org/) approach.

## License
Duuba XAdES is licensed under the Lesser General Public License V3 (LGPLv3) which is included in the license.txt in the root of the project.

Elements of the Bouncy Castle library provided by [The Legion of the Bouncy Castle Inc.](http://www.bouncycastle.org), see the bc_license.txt file.

## Support
Commercial Duuba XAdES support is provided by Chasquis. Contact [Chasquis-consulting.com](http://chasquis-consulting.com/) for more information.
