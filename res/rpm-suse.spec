Name:       openworld
Version:    1.1.9
Release:    0
Summary:    RPM package
License:    GPL-3.0
Requires:   gtk3 libxcb1 xdotool libXfixes3 alsa-utils libXtst6 libva2 pam gstreamer-plugins-base gstreamer-plugin-pipewire
Recommends: libayatana-appindicator3-1

# https://docs.fedoraproject.org/en-US/packaging-guidelines/Scriptlets/

%description
The best open-source remote desktop client software, written in Rust.

%prep
# we have no source, so nothing here

%build
# we have no source, so nothing here

%global __python %{__python3}

%install
mkdir -p %{buildroot}/usr/bin/
mkdir -p %{buildroot}/usr/share/openworld/
mkdir -p %{buildroot}/usr/share/openworld/files/
mkdir -p %{buildroot}/usr/share/icons/hicolor/256x256/apps/
mkdir -p %{buildroot}/usr/share/icons/hicolor/scalable/apps/
install -m 755 $HBB/target/release/openworld %{buildroot}/usr/bin/openworld
install $HBB/libsciter-gtk.so %{buildroot}/usr/share/openworld/libsciter-gtk.so
install $HBB/res/openworld.service %{buildroot}/usr/share/openworld/files/
install $HBB/res/128x128@2x.png %{buildroot}/usr/share/icons/hicolor/256x256/apps/openworld.png
install $HBB/res/scalable.svg %{buildroot}/usr/share/icons/hicolor/scalable/apps/openworld.svg
install $HBB/res/openworld.desktop %{buildroot}/usr/share/openworld/files/
install $HBB/res/openworld-link.desktop %{buildroot}/usr/share/openworld/files/

%files
/usr/bin/openworld
/usr/share/openworld/libsciter-gtk.so
/usr/share/openworld/files/openworld.service
/usr/share/icons/hicolor/256x256/apps/openworld.png
/usr/share/icons/hicolor/scalable/apps/openworld.svg
/usr/share/openworld/files/openworld.desktop
/usr/share/openworld/files/openworld-link.desktop

%changelog
# let's skip this for now

%pre
# can do something for centos7
case "$1" in
  1)
    # for install
  ;;
  2)
    # for upgrade
    systemctl stop openworld || true
  ;;
esac

%post
cp /usr/share/openworld/files/openworld.service /etc/systemd/system/openworld.service
cp /usr/share/openworld/files/openworld.desktop /usr/share/applications/
cp /usr/share/openworld/files/openworld-link.desktop /usr/share/applications/
systemctl daemon-reload
systemctl enable openworld
systemctl start openworld
update-desktop-database

%preun
case "$1" in
  0)
    # for uninstall
    systemctl stop openworld || true
    systemctl disable openworld || true
    rm /etc/systemd/system/openworld.service || true
  ;;
  1)
    # for upgrade
  ;;
esac

%postun
case "$1" in
  0)
    # for uninstall
    rm /usr/share/applications/openworld.desktop || true
    rm /usr/share/applications/openworld-link.desktop || true
    update-desktop-database
  ;;
  1)
    # for upgrade
  ;;
esac
