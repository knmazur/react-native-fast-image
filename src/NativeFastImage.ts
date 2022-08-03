// @flow
import type { TurboModule } from 'react-native/Libraries/TurboModule/RCTExport';
import { TurboModuleRegistry } from 'react-native';

export interface Spec extends TurboModule {
    // your module methods go here, for example:
    preload(sources: Array<string>): void;
    clearMemoryCache(): Promise<void>;
    clearDiskCache(): Promise<void>;
}
export default (TurboModuleRegistry.get<Spec>('FastImage')) as Spec;