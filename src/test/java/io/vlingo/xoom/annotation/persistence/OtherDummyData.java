// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.annotation.persistence;

import io.vlingo.xoom.annotation.model.OtherDummyState;

public class OtherDummyData {

    public final String name;

    public static OtherDummyData from(final OtherDummyState state) {
        return new OtherDummyData(state.name);
    }

    public OtherDummyData(final String name) {
        this.name = name;
    }

}
