<?php
 session_start();
 require_once('conf/command.php');
 require_once('../conf/conf.php');
 header("Expires: Mon, 26 Jul 1997 05:00:00 GMT"); 
 header("Last-Modified: ".gmdate("D, d M Y H:i:s")." GMT"); 
 header("Cache-Control: no-store, no-cache, must-revalidate"); 
 header("Cache-Control: post-check=0, pre-check=0", false);
 header("Pragma: no-cache"); // HTTP/1.0
?>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title><?php title();?></title>
    <link href="css/default.css" rel="stylesheet" type="text/css" />
    <script type="text/javascript" src="library/tiny_mce/tiny_mce.js"></script>
    <script type="text/javascript" src="conf/validator.js"></script>
    <script type="text/javascript" src="js/jquery/jquery.js"></script>
    <script type="text/javascript" src="js/jquery/jquery.dropdown.js"></script>
    <script>
        function PopupCenter(pageURL, title,w,h) {
            var left = (screen.width/2)-(w/2);
            var top = (screen.height/2)-(h/2);
            var targetWin = window.open (pageURL, title, 'toolbar=no, location=no, directories=no, status=no, menubar=no, scrollbars=no, resizable=no, copyhistory=no, width='+w+', height='+h+', top='+top+', left='+left);
            
        }
    </script>
</head>
<body>
    <div id="mainContent">
        <?php actionPages();?>
    </div>    
</body>
</html>

for (JsonNode list : root.path("Instances")) {
    System.out.println("Mengambil Gambar DCM " + koneksiDB.URLORTHANC() + ":" + koneksiDB.PORTORTHANC() + "/instances/" + list.asText() + "/file");
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "Basic " + authEncrypt);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_OCTET_STREAM));

    // Mengambil file DCM dari server Orthanc
    HttpEntity<String> entity = new HttpEntity<>(headers);
    ResponseEntity<byte[]> response = getRest().exchange(koneksiDB.URLORTHANC() + ":" + koneksiDB.PORTORTHANC() + "/instances/" + list.asText() + "/file", HttpMethod.GET, entity, byte[].class);

    // Menyimpan file DCM ke dalam folder gambarradiologi
    Files.write(Paths.get("./gambarradiologi/" + NoRawat + i + ".dcm"), response.getBody());
    
    // Mengirimkan file DCM ke URL yang ditentukan
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);
    FileSystemResource resource = new FileSystemResource("./gambarradiologi/" + NoRawat + i + ".dcm");
    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
    body.add("file", resource);
    HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
    ResponseEntity<String> uploadResponse = getRest().exchange("http://192.168.1.4/webappsnew1/radiologi/pages/upload", HttpMethod.POST, requestEntity, String.class);
    System.out.println("Response: " + uploadResponse.getBody());

    i++;
}


