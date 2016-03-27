# -*- mode: ruby -*-
# vi: set ft=ruby :

# Vagrantfile API/syntax version. Don't touch unless you know what you're doing!
VAGRANTFILE_API_VERSION = "2"

Vagrant.configure(VAGRANTFILE_API_VERSION) do |config|
  config.vm.define :app do |app|
    app.vm.box = "ubuntu/trusty64"
    app.vm.hostname = "app"
    app.vm.network "forwarded_port", guest:3000, host: 3030
    app.vm.provider "virtualbox" do |v|
      v.memory = 2048
      v.cpus = 2
    end
  end

  #config.vm.provision "shell", path: "setup.sh"
  config.vm.provision "ansible" do |ansible|
    ansible.groups = {
      "application" => ["app"]
    }
    ansible.playbook = "config/provision.yml"
  end
end
