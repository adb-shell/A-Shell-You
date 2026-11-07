Name:       openworld
Version:    1.4.0
Release:    0
Summary:    RPM package
License:    GPL-3.0
URL:        https://rustdesk.com
Vendor:     OpenWorld contributors
Requires:   gtk3 libxcb1 xdotool libXfixes3 alsa-utils libXtst6 libva2 pam gstreamer-plugins-base gstreamer-plugin-pipewire
Recommends: libayatana-appindicator3-1
Provides:   libdesktop_drop_plugin.so()(64bit), libdesktop_multi_window_plugin.so()(64bit), libfile_selector_linux_plugin.so()(64bit), libflutter_custom_cursor_plugin.so()(64bit), libflutter_linux_gtk.so()(64bit), libscreen_retriever_plugin.so()(64bit), libtray_manager_plugin.so()(64bit), liburl_launcher_linux_plugin.so()(64bit), libwindow_manager_plugin.so()(64bit), libwindow_size_plugin.so()(64bit), libtexture_rgba_renderer_plugin.so()(64bit)

# https://docs.fedoraproject.org/en-US/packaging-guidelines/Scriptlets/

%description
The best open-source remote desktop client software, written in Rust.

%prep
# we have no source, so nothing here

%build
# we have no source, so nothing here

# %global __python %{__python3}

%install

mkdir -p "%{buildroot}/usr/share/openworld" && cp -r ${HBB}/flutter/build/linux/x64/release/bundle/* -t "%{buildroot}/usr/share/openworld"
mkdir -p "%{buildroot}/usr/bin"
install -Dm 644 $HBB/res/openworld.service -t "%{buildroot}/usr/share/openworld/files"
install -Dm 644 $HBB/res/openworld.desktop -t "%{buildroot}/usr/share/openworld/files"
install -Dm 644 $HBB/res/openworld-link.desktop -t "%{buildroot}/usr/share/openworld/files"
install -Dm 644 $HBB/res/128x128@2x.png "%{buildroot}/usr/share/icons/hicolor/256x256/apps/openworld.png"
install -Dm 644 $HBB/res/scalable.svg "%{buildroot}/usr/share/icons/hicolor/scalable/apps/openworld.svg"

%files
/usr/share/openworld/*
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
ln -sf /usr/share/openworld/openworld /usr/bin/openworld
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
    rm /usr/bin/openworld || true
    rmdir /usr/lib/openworld || true
    rmdir /usr/local/openworld || true
    rmdir /usr/share/openworld || true
    rm /usr/share/applications/openworld.desktop || true
    rm /usr/share/applications/openworld-link.desktop || true
    update-desktop-database
  ;;
  1)
    # for upgrade
    rmdir /usr/lib/openworld || true
    rmdir /usr/local/openworld || true
  ;;
esac
