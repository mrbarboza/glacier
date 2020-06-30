# Instruções para executar

sudo docker build . -t spring
sudo docker run -p 8080:8080 -t spring

##Serviços
###Vault
/api/createVault
/api/deleteVault
/api/describeVault
/api/listVault
###Archive
/api/uploadArchive
/api/downloadArchive
/api/deleteArchive
