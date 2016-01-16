
#!/bin/sh
#echo $user $anotherUser $videoSource $vlcPath /etc/passwd
#echo "Grepping for $pattern"
rm /home/pi/partial_vids/video.101.h264
raspivid -w 640 -h 480 -hf -fps 30 -o /home/pi/partial_vids/video.101.h264 -t $duration
echo "Done recording."
echo "put /home/pi/partial_vids/video.101.h264 partial_vids/video.101.h264" | sftp rich@192.168.2.100
rm /home/pi/partial_vids/video.101.h264
echo "Done Grep"

