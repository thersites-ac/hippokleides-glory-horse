provider "aws" {
  region = "us-east-2"
}

resource "aws_instance" "bot" {
  ami = "ami-0f19d220602031aed"
  instance_type = "t2.micro"
  user_data = "${file("user-data.sh")}"
  key_name = "hippokleides"
}

output "public_ip" {
  value = aws_instance.bot.public_ip
}