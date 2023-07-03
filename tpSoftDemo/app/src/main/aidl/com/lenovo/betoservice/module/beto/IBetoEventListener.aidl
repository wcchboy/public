// IBetoEventListener.aidl
package com.lenovo.betoservice.module.beto;
import com.lenovo.betoservice.module.beto.model.BeToPhysicalDevice;
import com.lenovo.betoservice.module.beto.model.BeToFunctionDevice;

// Declare any non-default types here with import statements

interface IBetoEventListener {
    void onPhysicalDeviceChanged();
    void onPhysicalDevicePropChanged(in BeToPhysicalDevice physicalDevice, in List<String> props);
    void onFunctionDevicePropChanged(in BeToPhysicalDevice physicalDevice);
}
