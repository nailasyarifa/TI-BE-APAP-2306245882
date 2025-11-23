### **4. Pada EC2 instance yang kalian gunakan, kalian diperintahkan untuk mengaitkan dengan Elastic IP. Mengapa demikian? Lalu apa yang terjadi jika kalian tidak mengaitkan instance dengan Elastic IP?**

Elastic IP digunakan agar server mempunyai IP yang tetap, meskipun instance EC2 di restart atau dimatikan. Public IP default dari EC2 itu selalu berubah kalau instance stop–start, dan jika IP berubah, semua konfigurasi yang mengarah ke server misalnya seperti pipeline CI/CD, konfigurasi Kubernetes, serta koneksi database akan rusak. Jika tidak menggunakan Elastic IP, setiap kali instance reboot kita akan harus update ulang semua konfigurasi manual, yang pasti nya akan menambah beban kerja. Dengan menggunakan Elastic IP, server akan selalu  punya alamat IP yang stabil, sehingga seluruh pipeline tetap berjalan mulus.

### **5. Apa perbedaan utama dari penggunaan Docker dan Kubernetes pada praktikum ini?**

Di praktikum ini, Docker berfungsi untuk membuat dan menjalankan container aplikasi. Sementara Kubernetes digunakan untuk mengatur dan mengelola container tersebut ketika sudah dijalankan di server. Docker adalah “wadah aplikasinya”, sedangkan Kubernetes yang mengatur nya seperti auto-restart, scaling, load balancing, hingga routing melalui Service dan Ingress. Jadi Docker adalah runtime-nya, Kubernetes adalah pengelolanya.

### **6. Dari keseluruhan pipeline yang dibuat, menurutmu proses mana yang paling penting dan mengapa?**

Tahap yang paling penting adalah proses deployment ke server. Karena pada bagian ini, aplikasi yang sudah di-build benar-benar dijalankan di lingkungan server. Build image dan push ke registry memang penting, tapi kalau deployment bermasalah, aplikasi tetap tidak bisa diakses. Deployment juga bagian yang paling sensitif karena melibatkan konfigurasi Kubernetes, environment server, secret, dan image yang digunakan. Keberhasilan pipeline diukur dari hasil deployment yang berjalan dengan baik.

### **7. Pada konfigurasi Kubernetes, kalian menggunakan 5 file konfigurasi (3 pada folder k8s dan 2 dari .gitlab-ci.yml yaitu secret.yaml dan config.yaml). Buatkan penjelasan kegunaan dari kelima file tersebut!**

Tiga file di folder `k8s` berfungsi sebagai konfigurasi utama Kubernetes.

- **Deployment** mengatur container apa yang dijalankan, environment variables, jumlah replica, dan memastikan Pod tetap hidup.
- **Service** mengatur bagaimana Pod diakses di dalam cluster.
- **Ingress** menjadi pintu dari luar yang mengarahkan trafik HTTP/HTTPS ke Service.

Dua file lainnya, yaitu **secret.yaml** dan **config.yaml**, dibuat di pipeline GitLab:

- **Secret** menyimpan data sensitif seperti username/password database.
- **ConfigMap** menyimpan konfigurasi umum seperti URL backend.

Kelima file ini bekerja bersama untuk memastikan aplikasi berjalan stabil di dalam cluster.

### **8. Tanpa kalian sadari konfigurasi yang kalian lakukan baik untuk Docker database maupun Kubernetes sudah menerapkan “start on restart”. Jelaskan di bagian mana dan bagaimana hal itu diterapkan!**

Pada Docker database, otomatisasi *start on restart* terjadi karena kita memakai opsi seperti `restart: always` sehingga ketika server reboot, container database otomatis hidup lagi. Di Kubernetes, fitur ini sebenarnya bawaan dan Deployment selalu menjaga agar Pod tetap ada. Kalau server atau node di restart, kubelet akan menjalankan ulang Pod sesuai dengan deklarasi Deployment. Mekanisme  ini membuat aplikasi otomatis berjalan tanpa perlu di nyalakan manual.

### **9. Apa keuntungan dari menerapkan proses deployment kalian dibandingkan langsung run image docker saja di server?**

Deployment otomatis jauh lebih aman dan rapi dibanding menjalankan container secara manual dengan `docker run`. Dengan deployment, semua konfigurasi seperti environment variable, secret, image tag, dan pengaturan service tersimpan dan terkontrol. Jika terapat update, hanya dengan push kode dan pipeline akan menangani sisanya. Selain mengurangi human error, cara ini memudahkan tracking perubahan dan lebih cocok untuk aplikasi yang berkembang.

### **10. Jelaskan perbedaan antara ClusterIP, NodePort dan LoadBalancer. Dan mengapa ClusterIP merupakan pilihan yang sesuai untuk praktikum ini?**

- **ClusterIP**: hanya bisa diakses dari dalam cluster.
- **NodePort**: membuka port di setiap node agar bisa diakses dari luar melalui `<IP Node>:<Port>`.
- **LoadBalancer**: membuat load balancer eksternal (seperti AWS ELB) untuk mengarahkan trafik ke service.

Pada praktikum ini, ClusterIP adalah pilihan yang paling tepat karena aplikasi diakses melalui Ingress. Ingress membutuhkan service internal (ClusterIP) sebagai backend. Ini membuat arsitektur lebih aman dan tidak perlu membuka port server secara langsung.

### **11. Apa pelajaran terpenting yang kamu dapatkan dari proses deployment otomatis ini, dan bagaimana konsep CI/CD bisa diterapkan pada proyek lain?**

Pelajaran paling penting adalah bahwa otomatisasi membuat proses pengembangan jauh lebih mudah, rapi, dan konsisten, namun membutuhkan ketelitian yang cukup tingi. Dengan CI/CD, setiap perubahan kode langsung melalui proses build, test, dan deployment secara otomatis tanpa perlu masuk server manual. Konsep CI/CD bisa diterapkan di proyek manapun, baik backend, frontend, mobile, ataupun microservices gunanya untuk mempercepat workflow, mengurangi error, dan membuat proses rilis lebih profesional.

referensi :

*Kubernetes vs Docker - Perbedaan Antara Berbagai Teknologi Kontainer - AWS*. (n.d.). Amazon Web Services, Inc. <https://aws.amazon.com/id/compare/the-difference-between-kubernetes-and-docker/>

*Elastic IP addresses - Amazon Elastic Compute Cloud*. (n.d.). <https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/elastic-ip-addresses-eip.html>
