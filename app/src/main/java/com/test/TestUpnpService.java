package com.test;

import android.content.Intent;

import org.fourthline.cling.UpnpServiceConfiguration;
import org.fourthline.cling.android.AndroidNetworkAddressFactory;
import org.fourthline.cling.android.AndroidUpnpServiceConfiguration;
import org.fourthline.cling.android.AndroidUpnpServiceImpl;
import org.fourthline.cling.transport.spi.NetworkAddressFactory;

import java.net.NetworkInterface;
import java.util.Locale;

public class TestUpnpService extends AndroidUpnpServiceImpl {
  public static final int LISTEN_PORT = 31904;

  @Override
  protected UpnpServiceConfiguration createConfiguration() {
    return new TestAndroidUpnpServiceConfiguration(LISTEN_PORT);
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    super.onStartCommand(intent, flags, startId);
    return START_STICKY;
  }

  public class TestAndroidUpnpServiceConfiguration extends AndroidUpnpServiceConfiguration {

    public TestAndroidUpnpServiceConfiguration(int streamListenPort) {
      super(streamListenPort);
    }

    @Override
    protected NetworkAddressFactory createNetworkAddressFactory(int streamListenPort) {
      return new TestAndroidNetworkAddressFactory(streamListenPort);
    }
  }

  public class TestAndroidNetworkAddressFactory extends AndroidNetworkAddressFactory {
    public TestAndroidNetworkAddressFactory(int streamListenPort) {
      super(streamListenPort);
    }

    @Override
    protected boolean isUsableNetworkInterface(NetworkInterface iface) throws Exception {
      boolean result = super.isUsableNetworkInterface(iface);
      if (result) {
        if (iface.getName().toLowerCase(Locale.ENGLISH).startsWith("rmnet")) {
          result = false;
        }

        if (iface.getName().toLowerCase(Locale.ENGLISH).startsWith("p2p")) {
          result = false;
        }
      }

      return result;
    }
  }

  @Override
  public void onDestroy() {
    // Prevent crashes by RouterException
    // http://4thline.org/projects/mailinglists.html#nabble-td4025829
    try {
      upnpService.shutdown();
    } catch (RuntimeException e) {
      e.printStackTrace();
    }

    super.onDestroy();
  }
}
