<?php
include 'conn.php';

$response=array();

if(isset($_POST['image']))
{
    $u=$_POST['u'];
    $id=$_POST['id'];
    $name=$_POST['name'];
    $image=$_POST['image'];
    $imagename=$name.".jpeg";
    $path="coverpics/".$imagename;
    file_put_contents($path,base64_decode($image));
    $response['url']="mentorme/".$path;
    $response['message']="Image Uploaded Successfully";
    $response['status']=1;

    // Update the profilecover in the users table
    $query = "UPDATE users SET profilecover='$u/mentorme/$path' WHERE UserID=$id";
    mysqli_query($conn, $query);
    // if (mysqli_query($conn, $query)) {
    //     echo "Record updated successfully";
    // } else {
    //     echo "Error updating record: " . mysqli_error($conn);
    // }
}
else
{
    $response['message']="Incomplete Request";
    $response['status']=0;
}

echo json_encode($response);
?>