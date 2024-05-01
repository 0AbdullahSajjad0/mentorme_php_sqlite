<?php
include 'conn.php';

$response=array();

if(isset($_POST['image']))
{
    $name=$_POST['name'];
    $image=$_POST['image'];
    $imagename=$name.".jpeg";
    $path="profilepics/".$imagename;
    file_put_contents($path,base64_decode($image));
    $response['url']="mentorme/".$path;
    $response['message']="Image Uploaded Successfully";
    $response['status']=1;

}
else
{
    $response['message']="Incomplete Request";
    $response['status']=0;
}

echo json_encode($response);


?>