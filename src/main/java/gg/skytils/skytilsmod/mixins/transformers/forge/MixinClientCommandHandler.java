/*
 * Skytils - Hypixel Skyblock Quality of Life Mod
 * Copyright (C) 2020-2024 Skytils
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package gg.skytils.skytilsmod.mixins.transformers.forge;

import gg.skytils.skytilsmod.features.impl.handlers.NamespacedCommands;
import gg.skytils.skytilsmod.mixins.transformers.accessors.AccessorCommandHandler;
import gg.skytils.skytilsmod.utils.ObservableSet;
import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommand;
import net.minecraftforge.client.ClientCommandHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ClientCommandHandler.class)
public abstract class MixinClientCommandHandler extends CommandHandler implements AccessorCommandHandler {
    @Inject(method = "<init>", at = @At("RETURN"))
    private void hijackCommandHandler(CallbackInfo ci) {
        ObservableSet<ICommand> hijacked = new ObservableSet<>(this.getCommandSet());
        NamespacedCommands.INSTANCE.setup(hijacked);
        this.setCommandSet(hijacked);
    }
}
